package anissia.services

import anissia.configruration.logger
import anissia.misc.As
import anissia.rdb.domain.ActivePanel
import anissia.rdb.domain.Agenda
import anissia.rdb.domain.Anime
import anissia.rdb.domain.AnimeCaption
import anissia.rdb.dto.AdminCaptionDto
import anissia.rdb.dto.AnimeDto
import anissia.rdb.dto.ResultStatus
import anissia.rdb.dto.request.AnimeCaptionRequest
import anissia.rdb.dto.request.AnimeRequest
import anissia.rdb.repository.AgendaRepository
import anissia.rdb.repository.AnimeCaptionRepository
import anissia.rdb.repository.AnimeGenreRepository
import anissia.rdb.repository.AnimeRepository
import me.saro.kit.lang.Koreans
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.Column
import javax.persistence.Lob
import javax.servlet.http.HttpServletRequest

@Service
class AdminService(
    private val animeRepository: AnimeRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeGenreRepository: AnimeGenreRepository,
    private val request: HttpServletRequest,
    private val sessionService: SessionService,
    private val activePanelService: ActivePanelService,
    private val animeService: AnimeService,
    private val agendaService: AgendaService,
    private val agendaRepository: AgendaRepository
) {

    val log = logger<AnimeService>()
    val user get() = sessionService.session
    val userAn get() = user?.an ?: 0
    val userName get() = user?.name ?: 0

    fun getCaptionList(active: Int, page: Int): Page<AdminCaptionDto> = (
            if (active == 1) animeCaptionRepository.findAllWithAnimeForAdminCaptionActiveList(userAn, PageRequest.of(page, 20))
            else animeCaptionRepository.findAllWithAnimeForAdminCaptionEndList(userAn, PageRequest.of(page, 20))
            ).map { AdminCaptionDto(it) }

    fun addAnime(animeRequest: AnimeRequest) = updateAnime(0, animeRequest)

    fun updateAnime(animeNo: Long, animeRequest: AnimeRequest): ResultStatus {
        val isNew = animeNo == 0L
        animeRequest.validate()
        if (animeGenreRepository.countByGenreIn(animeRequest.genresList).toInt() != animeRequest.genresList.size) {
            return ResultStatus("FAIL", "장르 입력이 잘못되었습니다.")
        }

        var anime: Anime
        var activePanel = ActivePanel(published = true, code = "ANIME", status = if (isNew) "C" else "U", an = userAn, data1 = "")

        if (isNew) {
            if (animeRepository.existsBySubject(animeRequest.subject)) {
                return ResultStatus("FAIL", "이미 동일한 이름의 작품이 존재합니다.")
            }
            anime = Anime(
                status = animeRequest.statusEnum,
                week = animeRequest.week,
                time = animeRequest.time,
                subject = animeRequest.subject,
                autocorrect = Koreans.toJasoAtom(animeRequest.subject),
                genres = animeRequest.genres,
                startDate = animeRequest.startDate,
                endDate = animeRequest.endDate,
                website = animeRequest.website,
            )
        } else {
            anime = animeRepository.findByIdOrNull(animeNo)
                ?.also { activePanel.data2 = As.toJsonString(AnimeDto(it, false)) }
                ?.apply {
                    status = animeRequest.statusEnum
                    week = animeRequest.week
                    time = animeRequest.time
                    subject = animeRequest.subject
                    autocorrect = Koreans.toJasoAtom(animeRequest.subject)
                    genres = animeRequest.genres
                    startDate = animeRequest.startDate
                    endDate = animeRequest.endDate
                    website = animeRequest.website
                }
                ?.also { activePanel.data3 = As.toJsonString(AnimeDto(it, false)) }
                ?: return ResultStatus("FAIL", "존재하지 않는 애니메이션입니다.")
        }

        animeRepository.save(anime)

        if (isNew) {
            activePanel.data1 = "[$userName]님이 애니메이션 [${anime.subject}]을(를) 추가하였습니다."
        } else {
            activePanel.data1 = "[$userName]님이 애니메이션 [${anime.subject}]을(를) 수정하였습니다."
        }

        activePanelService.save(activePanel)
        animeService.updateDocument(anime)

        return ResultStatus("OK")
    }

    @Transactional
    fun deleteAnime(animeNo: Long): ResultStatus {
        val agenda = Agenda(code = "ANIME-DEL", status = "WAIT", an = userAn)

        val anime = animeRepository.findWithCaptionsByAnimeNo(animeNo)
            ?.also { agenda.data1 = As.toJsonString(AnimeDto(it, true)) }
            ?: return ResultStatus("FAIL", "존재하지 않는 애니메이션입니다.")

        activePanelService.saveText("[$userName]님이 애니메이션 [${anime.subject}]을(를) 삭제하였습니다.", true)

        animeCaptionRepository.deleteByAnimeNo(animeNo)
        animeRepository.delete(anime)
        animeService.deleteDocument(animeNo)
        agendaRepository.save(agenda)

        return ResultStatus("OK")
    }

    @Transactional
    fun addCaption(animeNo: Long) = updateCaption(animeNo, AnimeCaptionRequest(), true)

    @Transactional
    fun updateCaption(animeNo: Long, caption: AnimeCaptionRequest, isNew: Boolean = false): ResultStatus {
        log.info("update caption: $animeNo $caption")
        caption.validate()

        var animeCaption: AnimeCaption

        if (isNew) {
            if (animeCaptionRepository.findById(AnimeCaption.Key(animeNo, userAn)).isPresent) {
                return ResultStatus("FAIL", "이미 작업중인 작품입니다.")
            }
            animeCaption = AnimeCaption(
                animeNo = animeNo,
                an = userAn,
                episode = caption.episode,
                updDt = caption.updLdt,
                website = caption.website,
            )
        } else {
            animeCaption = animeCaptionRepository.findByIdOrNull(AnimeCaption.Key(animeNo, userAn))
                ?.apply {
                    episode = caption.episode
                    updDt = caption.updLdt
                    website = caption.website
                }
                ?: return ResultStatus("FAIL", "존재하지 않는 자막입니다.")
        }

        val anime = animeRepository.findByIdOrNull(animeNo)
            ?: return ResultStatus("FAIL", "존재하지 않는 애니메이션입니다.")

        animeCaptionRepository.save(animeCaption)

        if (isNew) {
            animeRepository.updateCaptionCount(animeNo)
            activePanelService.saveText("[$userName]님이 [${anime.subject}] 자막을 시작하였습니다.")
        }

        return ResultStatus("OK", "수정되었습니다.")
    }

    @Transactional
    fun deleteCaption(animeNo: Long) =
        animeCaptionRepository.findByIdOrNull(AnimeCaption.Key(animeNo, userAn))
            ?.run {
                animeCaptionRepository.delete(this)
                animeRepository.updateCaptionCount(animeNo)
                activePanelService.saveText("[$userName]님이 [${anime?.subject}] 자막을 종료하였습니다.")
                ResultStatus("OK")
            }
            ?: ResultStatus("FAIL", "이미 삭제되었습니다.")
}