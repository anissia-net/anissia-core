package anissia.domain.anime.service

import anissia.domain.account.Account
import anissia.domain.activePanel.command.AddTextActivePanelCommand
import anissia.domain.activePanel.service.ActivePanelService
import anissia.domain.anime.Anime
import anissia.domain.anime.AnimeCaption
import anissia.domain.anime.command.*
import anissia.domain.anime.model.CaptionItem
import anissia.domain.anime.model.CaptionRecentItem
import anissia.domain.anime.model.MyCaptionItem
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.anime.repository.AnimeRepository
import anissia.domain.session.model.SessionItem
import anissia.infrastructure.common.MonoCacheStore
import anissia.infrastructure.common.doOnNextMono
import anissia.infrastructure.common.mapPageItem
import anissia.infrastructure.common.subscribeBoundedElastic
import anissia.shared.ApiErrorException
import anissia.shared.ApiFailException
import anissia.shared.ApiResponse
import me.saro.kit.service.CacheStore
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.ZoneOffset

@Service
class CaptionServiceImpl(
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeRepository: AnimeRepository,
    private val animeDocumentService: AnimeDocumentService,
    private val activePanelService: ActivePanelService,
    private val animeRankService: AnimeRankService,
): CaptionService {

    private val recentListStore = MonoCacheStore<Int, Page<CaptionRecentItem>>(5 * 60000)

    override fun getList(cmd: GetListCaptionByAnimeNoCommand, sessionItem: SessionItem): Mono<List<CaptionItem>> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .flatMapMany { animeCaptionRepository.findAllWithAccountByAnimeNoOrderByUpdDtDesc(cmd.animeNo) }
            .map { CaptionItem(it) }
            .collectList()

    override fun getList(cmd: GetMyListCaptionCommand, sessionItem: SessionItem): Mono<Page<MyCaptionItem>> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .doOnNext { sessionItem.validateAdmin() }
            .flatMap {
                if (it.active == 1) {
                    animeCaptionRepository.findAllWithAnimeForAdminCaptionActiveList(
                        sessionItem.an,
                        PageRequest.of(cmd.page, 20)
                    )
                } else {
                    animeCaptionRepository.findAllWithAnimeForAdminCaptionEndList(
                        sessionItem.an,
                        PageRequest.of(cmd.page, 20)
                    )
                }
            }
            .map { it.map { MyCaptionItem(it) } }

    override fun getList(cmd: GetRecentListCaptionCommand): Mono<Page<CaptionRecentItem>> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .flatMap {
                // page -1 인 경우 최근 12개만 가져온다.
                // page 가 0 이상인 경우 20개 단위로 페이징하여 가져온다.
                if (it.page == -1) {
                    recentListStore.find(it.page) {
                        animeCaptionRepository.findAllByUpdDtAfterAndWebsiteNotOrderByUpdDtDesc(PageRequest.of(0, 12))
                            .mapPageItem { CaptionRecentItem(it) }
                    }
                } else {
                    animeCaptionRepository.findAllByUpdDtAfterAndWebsiteNotOrderByUpdDtDesc(
                        PageRequest.of(
                            cmd.page,
                            20
                        )
                    ).mapPageItem { CaptionRecentItem(it) }
                }
            }

    @Transactional
    override fun add(cmd: AddCaptionCommand, sessionItem: SessionItem): Mono<String> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .doOnNext { sessionItem.validateAdmin() }
            .flatMap { animeRepository.findById(cmd.animeNo) }
            .switchIfEmpty(Mono.error(ApiErrorException("존재하지 않는 애니메이션입니다.")))
            .flatMap { anime ->
                animeCaptionRepository.findById(AnimeCaption.Key(cmd.animeNo, sessionItem.an))
                    .flatMap { Mono.error<String>(ApiFailException("이미 작업중인 작품입니다.")) }
                    .thenReturn(AnimeCaption(anime = anime, an = sessionItem.an))
                    .flatMap { animeCaptionRepository.save(it).mapNotNull<Anime> { item -> item.anime } }
                    .doOnNextMono { animeRepository.updateCaptionCount(it.animeNo) }
                    .doOnNext { animeDocumentService.update(it, false).subscribeBoundedElastic() }
                    .flatMap {
                        activePanelService.addText(
                            AddTextActivePanelCommand(true, "[${sessionItem.name}]님이 [${anime.subject}] 자막을 시작하였습니다."),
                            null
                        )
                    }
                    .map { "자막을 추가하였습니다.\n자막메뉴에서 확인해주세요." }
            }

    @Transactional
    override fun edit(cmd: EditCaptionCommand, sessionItem: SessionItem): Mono<String> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .doOnNext { sessionItem.validateAdmin() }
            .flatMap { animeCaptionRepository.findById(AnimeCaption.Key(it.animeNo, sessionItem.an)) }
            .switchIfEmpty(Mono.error(ApiErrorException("존재하지 않는 자막입니다.")))
            .flatMap { caption ->
                animeCaptionRepository.save(caption.apply {
                    edit(
                        episode = cmd.episode,
                        updDt = cmd.updLdt.atOffset(ZoneOffset.ofHours(9)),
                        website = cmd.website
                    )
                })
            }
            .map { "자막정보가 반영되었습니다." }


    @Transactional
    override fun delete(cmd: DeleteCaptionCommand, sessionItem: SessionItem): Mono<String> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .doOnNext { sessionItem.validateAdmin() }
            .flatMap {
                animeCaptionRepository.findById(AnimeCaption.Key(cmd.animeNo, sessionItem.an))
                    .zipWhen { Mono.just(it.anime!!) }
            }
            .flatMap { zip ->
                activePanelService.addText(
                    AddTextActivePanelCommand(
                        true,
                        "[${sessionItem.name}]님이 [${zip.t2.subject}] 자막을 종료하였습니다."
                    ), null
                ).thenReturn(zip.t1)
            }
            .flatMap { animeCaptionRepository.delete(it).thenReturn(it) }
            .flatMap { animeRepository.updateCaptionCount(cmd.animeNo) }
            .flatMap { animeDocumentService.update(UpdateAnimeDocumentCommand(cmd.animeNo)).map { "" } }
            .switchIfEmpty(Mono.error(ApiErrorException("이미 삭제되었습니다.")))

    @Transactional
    override fun delete(account: Account, sessionItem: SessionItem): Mono<Int> =
        Mono.just(sessionItem)
            .doOnNext { it.validateAdmin() }
            .flatMap {
                animeCaptionRepository.findAllWithAnimeByAn(account.an).collectList()
                    .flatMap { captionList ->
                        animeCaptionRepository.deleteAll(captionList).thenReturn(captionList.map { it.anime })
                    }
                    .doOnNextMono { animeList ->
                        animeRepository.updateCaptionCountByIds(animeList.mapNotNull { it?.animeNo })
                            .doOnNext {
                                animeList.forEach { anime ->
                                    animeDocumentService.update(anime!!, false).subscribeBoundedElastic()
                                }
                            }
                    }
                    .map { it.count() }
            }
}
