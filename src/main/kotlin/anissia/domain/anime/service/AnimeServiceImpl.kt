package anissia.domain.anime.service

import anissia.domain.account.repository.AccountRepository
import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.command.AddTextActivePanelCommand
import anissia.domain.activePanel.repository.ActivePanelRepository
import anissia.domain.activePanel.service.ActivePanelLogService
import anissia.domain.agenda.Agenda
import anissia.domain.agenda.repository.AgendaRepository
import anissia.domain.anime.Anime
import anissia.domain.anime.AnimeCaption
import anissia.domain.anime.AnimeStatus
import anissia.domain.anime.command.*
import anissia.domain.anime.model.AnimeItem
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.anime.repository.AnimeDocumentRepository
import anissia.domain.anime.repository.AnimeGenreRepository
import anissia.domain.anime.repository.AnimeRepository
import anissia.domain.session.model.SessionItem
import anissia.domain.translator.service.TranslatorApplyService
import anissia.infrastructure.common.As
import anissia.shared.ResultWrapper
import com.fasterxml.jackson.core.type.TypeReference
import me.saro.kit.lang.KoreanKit
import me.saro.kit.service.CacheStore
import org.elasticsearch.client.Response
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class AnimeServiceImpl(
    private val animeRepository: AnimeRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeDocumentService: AnimeDocumentService,
    private val animeDocumentRepository: AnimeDocumentRepository,
    private val animeRankService: AnimeRankService,
    private val activePanelLogService: ActivePanelLogService,
    private val agendaRepository: AgendaRepository,
    private val translatorApplyService: TranslatorApplyService,
    private val animeGenreRepository: AnimeGenreRepository,
    private val activePanelRepository: ActivePanelRepository,
    private val accountRepository: AccountRepository,
): AnimeService {

    private val log = As.logger<AnimeServiceImpl>()
    private val mapper = As.OBJECT_MAPPER

    private val autocorrectStore = CacheStore<String, List<String>>(60 * 60000)

    override fun get(cmd: GetAnimeCommand, sessionItem: SessionItem): AnimeItem =
        animeRepository.findWithCaptionsByAnimeNo(cmd.animeNo)
            ?.let { AnimeItem(it, true) }
            ?.also { animeRankService.hit(HitAnimeCommand(cmd.animeNo), sessionItem) }
            ?: AnimeItem()

    override fun getList(cmd: GetAnimeListCommand): Page<AnimeItem> {
        val q = cmd.q
        val page = cmd.page

        if (q.isNotBlank()) {
            val res: Response = animeDocumentRepository.search(q, page)
            val hits = res.entity.content.bufferedReader().use { mapper.readTree(it) }["hits"]
            val result = PageImpl<Long>(hits["hits"].map { it["_id"].asLong() }, PageRequest.of(page, 30), hits["total"]["value"].asLong())

            log.info("anime search $q: ${result.totalElements} items")

            return As.replacePage(result, animeRepository.findAllByAnimeNoInOrderByAnimeNoDesc(result.content).map { AnimeItem(it) })
        } else {
            return animeRepository.findAllByOrderByAnimeNoDesc(PageRequest.of(page, 30)).map { AnimeItem(it) }
        }
    }

    override fun getDelist(sessionItem: SessionItem): Page<AnimeItem> {
        sessionItem.validateAdmin()

        return agendaRepository.findAllByCodeAndStatusOrderByAgendaNoDesc("ANIME-DEL", "wait")
            .map { As.OBJECT_MAPPER.readValue(it.data1!!, object: TypeReference<AnimeItem>(){}).apply { this.agendaNo = it.agendaNo } }
    }

    override fun getAutocorrect(cmd: GetAutocorrectAnimeCommand): List<String> {
        val q = cmd.q
        return if (q.length < 3) autocorrectStore.find(q) { getAnimeAutocorrectPrivate(q) }
        else  getAnimeAutocorrectPrivate(q)
    }
    private fun getAnimeAutocorrectPrivate(q: String): List<String> =
        q.replace("%", "").trim()
            .takeIf { it.isNotEmpty() }
            ?.let { animeRepository.findTop10ByAutocorrectStartsWith(KoreanKit.toJasoAtom(it)) }
            ?: listOf()

    @Transactional
    override fun add(cmd: NewAnimeCommand, sessionItem: SessionItem): ResultWrapper<Long> {
        cmd.validate()
        sessionItem.validateAdmin()
        translatorApplyService.getGrantedTime(sessionItem.an)
            ?.takeIf { it.isBefore(OffsetDateTime.now().minusDays(90)) }
            ?: return ResultWrapper.fail("애니메이션 등록은 권한 취득일로부터 90일 후에 가능합니다.", -1)

        if (animeGenreRepository.countByGenreIn(cmd.genresList).toInt() != cmd.genresList.size) {
            return ResultWrapper.fail("장르 입력이 잘못되었습니다.", -1)
        }

        if (animeRepository.existsBySubject(cmd.subject)) {
            return ResultWrapper.fail("이미 동일한 이름의 작품이 존재합니다.", -1)
        }

        val anime = Anime(
            status = cmd.statusEnum,
            week = cmd.week,
            time = cmd.time,
            subject = cmd.subject,
            originalSubject = cmd.originalSubject,
            autocorrect = KoreanKit.toJasoAtom(cmd.subject),
            genres = cmd.genres,
            startDate = cmd.startDate,
            endDate = cmd.endDate,
            website = cmd.website,
            twitter = cmd.twitter,
        )

        val activePanel = ActivePanel(
            published = true,
            code = "ANIME",
            status = "C",
            an = sessionItem.an,
            data1 = "[${sessionItem.name}]님이 애니메이션 [${anime.subject}]을(를) 추가하였습니다."
        )

        animeRepository.save(anime)
        activePanelRepository.save(activePanel)
        animeDocumentService.update(anime)

        return ResultWrapper.ok(anime.animeNo)
    }

    @Transactional
    override fun edit(cmd: EditAnimeCommand, sessionItem: SessionItem): ResultWrapper<Long> {
        cmd.validate()
        sessionItem.validateAdmin()
        translatorApplyService.getGrantedTime(sessionItem.an)
            ?.takeIf { it.isBefore(OffsetDateTime.now().minusDays(90)) }
            ?: return ResultWrapper.fail("애니메이션 편집은 권한 취득일로부터 90일 후에 가능합니다.", -1)

        val animeNo = cmd.animeNo

        if (animeGenreRepository.countByGenreIn(cmd.genresList).toInt() != cmd.genresList.size) {
            return ResultWrapper.fail("장르 입력이 잘못되었습니다.", -1)
        }

        if (animeRepository.existsBySubjectAndAnimeNoNot(cmd.subject, animeNo)) {
            return ResultWrapper.fail("이미 동일한 이름의 작품이 존재합니다.", -1)
        }

        val activePanel = ActivePanel(
            published = true,
            code = "ANIME",
            status = "U",
            an = sessionItem.an,
            data1 = "[${sessionItem.name}]님이 애니메이션 [${cmd.subject}]을(를) 수정하였습니다."
        )

        val anime = animeRepository.findByIdOrNull(animeNo)
            ?.also {
                if (
                    it.week == cmd.week &&
                    it.status == cmd.statusEnum &&
                    it.time == cmd.time &&
                    it.subject == cmd.subject &&
                    it.originalSubject == cmd.originalSubject &&
                    it.genres == cmd.genres &&
                    it.startDate == cmd.startDate &&
                    it.endDate == cmd.endDate &&
                    it.website == cmd.website &&
                    it.twitter == cmd.twitter &&
                    it.note == cmd.note
                ) {
                    return ResultWrapper.fail("변경사항이 없습니다.", -1)
                }
            }
            ?.also { activePanel.data2 = As.toJsonString(AnimeItem(it, false)) }
            ?.apply {
                status = cmd.statusEnum
                week = cmd.week
                time = cmd.time
                subject = cmd.subject
                originalSubject = cmd.originalSubject
                autocorrect = KoreanKit.toJasoAtom(cmd.subject)
                genres = cmd.genres
                startDate = cmd.startDate
                endDate = cmd.endDate
                website = cmd.website
                twitter = cmd.twitter
                note = cmd.note
            }
            ?.also { activePanel.data3 = As.toJsonString(AnimeItem(it, false)) }
            ?: return ResultWrapper.fail("존재하지 않는 애니메이션입니다.", -1)

        animeRepository.save(anime)
        activePanelRepository.save(activePanel)
        animeDocumentService.update(anime)

        return ResultWrapper.of("ok", "", anime.animeNo)
    }

    @Transactional
    override fun delete(cmd: DeleteAnimeCommand, sessionItem: SessionItem): ResultWrapper<Unit> {
        cmd.validate()
        sessionItem.validateAdmin()
        translatorApplyService.getGrantedTime(sessionItem.an)
            ?.takeIf { it.isBefore(OffsetDateTime.now().minusDays(90)) }
            ?: return ResultWrapper.fail("애니메이션 삭제는 권한 취득일로부터 90일 후에 가능합니다.")

        val animeNo = cmd.animeNo
        val agenda = Agenda(code = "ANIME-DEL", status = "wait", an = sessionItem.an)

        val anime = animeRepository.findWithCaptionsByAnimeNo(animeNo)
            ?.also { agenda.data1 = As.toJsonString(AnimeItem(it, true)) }
            ?: return ResultWrapper.fail("존재하지 않는 애니메이션입니다.")

        activePanelLogService.addText(AddTextActivePanelCommand("[${sessionItem.name}]님이 애니메이션 [${anime.subject}]을(를) 삭제하였습니다."), null)

        animeCaptionRepository.deleteByAnimeNo(animeNo)
        animeRepository.delete(anime)
        animeDocumentService.update(anime)
        agendaRepository.save(agenda)

        return ResultWrapper.ok()
    }

    @Transactional
    override fun recover(cmd: RecoverAnimeCommand, sessionItem: SessionItem): ResultWrapper<Long> {
        cmd.validate()
        sessionItem.validateAdmin()

        val agenda = agendaRepository
            .findByIdOrNull(cmd.agendaNo)?.takeIf { it.code == "ANIME-DEL" && it.status == "wait" }
            ?: return ResultWrapper.fail("이미 복원되었거나 존재하지 않는 애니메이션입니다.", -1)

        val animeItem = As.OBJECT_MAPPER.readValue(agenda.data1, object: TypeReference<AnimeItem>() {})

        if (animeRepository.existsById(animeItem.animeNo)) {
            return ResultWrapper.fail("이미 복원되었거나 존재하지 않는 애니메이션입니다.", -1)
        }
        if (animeRepository.existsBySubject(animeItem.subject)) {
            return ResultWrapper.fail("이미 해당 제목의 에니메이션이 있습니다.", -1)
        }

        val anime = animeRepository.save(
            Anime(
                status = AnimeStatus.valueOf(animeItem.status),
                week = animeItem.week,
                time = animeItem.time,
                subject = animeItem.subject,
                originalSubject = animeItem.originalSubject,
                autocorrect = KoreanKit.toJasoAtom(animeItem.subject),
                genres = animeItem.genres,
                startDate = animeItem.startDate,
                endDate = animeItem.endDate,
                website = animeItem.website,
                twitter = animeItem.twitter,
                captionCount = animeItem.captionCount
            )
        )

        animeItem.captions.forEach { caption ->
            val account = accountRepository.findWithRolesByName(caption.name)
            if (account?.isAdmin == true) {
                animeCaptionRepository.save(
                    AnimeCaption(
                        anime = anime,
                        an = account.an,
                        episode = caption.episode,
                        updDt = OffsetDateTime.parse(caption.updDt, As.DTF_ISO_YMDHMS),
                        website = caption.website
                    )
                )
            }
        }

        activePanelLogService.addText(AddTextActivePanelCommand("[${sessionItem.name}]님이 애니메이션 [${anime.subject}]을(를) 복원하였습니다."), null)

        animeDocumentService.update(anime)
        animeRepository.updateCaptionCount(anime.animeNo)
        agendaRepository.save(agenda.apply { status = "recover" })

        return ResultWrapper.of("ok", "", anime.animeNo)
    }
}
