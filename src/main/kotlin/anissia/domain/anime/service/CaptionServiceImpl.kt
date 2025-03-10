package anissia.domain.anime.service

import anissia.domain.account.Account
import anissia.domain.activePanel.command.AddTextActivePanelCommand
import anissia.domain.activePanel.service.ActivePanelLogService
import anissia.domain.anime.AnimeCaption
import anissia.domain.anime.command.*
import anissia.domain.anime.model.CaptionItem
import anissia.domain.anime.model.CaptionRecentItem
import anissia.domain.anime.model.MyCaptionItem
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.anime.repository.AnimeRepository
import anissia.domain.session.model.SessionItem
import anissia.shared.ResultWrapper
import me.saro.kit.service.CacheStore
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneOffset

@Service
class CaptionServiceImpl(
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeRepository: AnimeRepository,
    private val animeDocumentService: AnimeDocumentService,
    private val activePanelLogService: ActivePanelLogService,
    private val animeRankService: AnimeRankService,
): CaptionService {

    private val recentListStore = CacheStore<Int, Page<CaptionRecentItem>>(5 * 60000)

    override fun getList(cmd: GetListCaptionByAnimeNoCommand, sessionItem: SessionItem): List<CaptionItem> {
        cmd.validate()
        return animeCaptionRepository.findAllWithAccountByAnimeNoOrderByUpdDtDesc(cmd.animeNo).map { CaptionItem(it) }
            .also { animeRankService.hit(HitAnimeCommand(cmd.animeNo), sessionItem) }
    }

    override fun getList(cmd: GetMyListCaptionCommand, sessionItem: SessionItem): Page<MyCaptionItem> {
        cmd.validate()
        sessionItem.validateAdmin()

        return if (cmd.active == 1) {
            animeCaptionRepository.findAllWithAnimeForAdminCaptionActiveList(sessionItem.an, PageRequest.of(cmd.page, 20))
        } else {
            animeCaptionRepository.findAllWithAnimeForAdminCaptionEndList(sessionItem.an, PageRequest.of(cmd.page, 20))
        }.map { MyCaptionItem(it) }
    }

    override fun getList(cmd: GetRecentListCaptionCommand): Page<CaptionRecentItem> {
        cmd.validate()

        // page -1 인 경우 최근 12개만 가져온다.
        // page 가 0 이상인 경우 20개 단위로 페이징하여 가져온다.
        return if (cmd.page == -1) {
            recentListStore.find(cmd.page) {
                animeCaptionRepository.findAllByUpdDtAfterAndWebsiteNotOrderByUpdDtDesc(PageRequest.of(0, 12)).map { CaptionRecentItem(it) }
            }
        } else {
            animeCaptionRepository.findAllByUpdDtAfterAndWebsiteNotOrderByUpdDtDesc(PageRequest.of(cmd.page, 20)).map { CaptionRecentItem(it) }
        }
    }

    @Transactional
    override fun add(cmd: AddCaptionCommand, sessionItem: SessionItem): ResultWrapper<Unit> {
        cmd.validate()
        sessionItem.validateAdmin()

        val animeNo = cmd.animeNo

        val anime = animeRepository.findByIdOrNull(animeNo)
            ?: return ResultWrapper.fail("존재하지 않는 애니메이션입니다.")

        if (animeCaptionRepository.findById(AnimeCaption.Key(animeNo, sessionItem.an)).isPresent) {
            return ResultWrapper.fail("이미 작업중인 작품입니다.")
        }

        animeCaptionRepository.save(AnimeCaption(anime = anime, an = sessionItem.an))
        animeRepository.updateCaptionCount(animeNo)
        animeDocumentService.update(anime)
        activePanelLogService.addText(AddTextActivePanelCommand("[${sessionItem.name}]님이 [${anime.subject}] 자막을 시작하였습니다.", true), null)

        return ResultWrapper.of("ok", "자막을 추가하였습니다.\n자막메뉴에서 확인해주세요.")
    }

    @Transactional
    override fun edit(cmd: EditCaptionCommand, sessionItem: SessionItem): ResultWrapper<Unit> {
        cmd.validate()
        sessionItem.validateAdmin()

        val animeNo = cmd.animeNo

        val caption = animeCaptionRepository.findByIdOrNull(AnimeCaption.Key(animeNo, sessionItem.an))
            ?: return ResultWrapper.fail("존재하지 않는 자막입니다.")

        animeCaptionRepository.save(caption.apply {
            edit(
                episode = cmd.episode,
                updDt = cmd.updLdt.atOffset(ZoneOffset.ofHours(9)),
                website = cmd.website
            )
        })

        return ResultWrapper.of("ok", "자막정보가 반영되었습니다.")
    }

    @Transactional
    override fun delete(cmd: DeleteCaptionCommand, sessionItem: SessionItem): ResultWrapper<Unit> {
        cmd.validate()
        sessionItem.validateAdmin()

        val animeNo = cmd.animeNo

        return animeCaptionRepository.findByIdOrNull(AnimeCaption.Key(animeNo, sessionItem.an))
            ?.run {
                animeCaptionRepository.delete(this)
                animeRepository.updateCaptionCount(animeNo)
                animeDocumentService.update(UpdateAnimeDocumentCommand(animeNo))
                activePanelLogService.addText(AddTextActivePanelCommand("[${sessionItem.name}]님이 [${anime?.subject}] 자막을 종료하였습니다.", true), null)
                ResultWrapper.ok()
            }
            ?: ResultWrapper.fail("이미 삭제되었습니다.")
    }

    @Transactional
    override fun delete(account: Account, sessionItem: SessionItem): Int {
        sessionItem.validateRoot()
        val captionList: List<AnimeCaption> = animeCaptionRepository.findAllByAn(account.an)
        val animeNoList: List<Long> = captionList.map { it.anime!!.animeNo }
        val animeList = animeRepository.findAllByIds(animeNoList)
        animeCaptionRepository.deleteAll(captionList)
        animeCaptionRepository.flush()
        animeRepository.updateCaptionCountByIds(animeNoList)
        animeList.parallelStream().forEach { animeDocumentService.update(it) }
        return captionList.count()
    }
}
