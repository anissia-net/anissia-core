package anissia.services

import anissia.configruration.logger
import anissia.rdb.domain.AnimeCaption
import anissia.rdb.dto.AdminCaptionDto
import anissia.rdb.dto.ResultStatus
import anissia.rdb.dto.request.AdminCaptionRequest
import anissia.rdb.repository.AnimeCaptionRepository
import anissia.rdb.repository.AnimeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class AdminService(
    private val animeRepository: AnimeRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val request: HttpServletRequest,
    private val sessionService: SessionService,
    private val activePanelService: ActivePanelService
) {

    val log = logger<AnimeService>()
    val user get() = sessionService.session
    val userAn get() = user?.an ?: 0
    val userName get() = user?.name ?: 0

    fun getCaptionList(active: Int, page: Int): Page<AdminCaptionDto> = (
            if (active == 1) animeCaptionRepository.findAllWithAnimeForAdminCaptionActiveList(userAn, PageRequest.of(page, 20))
            else animeCaptionRepository.findAllWithAnimeForAdminCaptionEndList(userAn, PageRequest.of(page, 20))
            ).map { AdminCaptionDto(it) }


    fun addCaption(animeNo: Long) = updateCaption(animeNo, AdminCaptionRequest(), true)

    fun updateCaption(animeNo: Long, caption: AdminCaptionRequest, isNew: Boolean = false): ResultStatus {
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
            activePanelService.saveText("$userName 님이 ${anime.subject} 자막을 시작하였습니다.")
        }

        return ResultStatus("OK", "수정되었습니다.")
    }

    fun deleteCaption(animeNo: Long) =
        animeCaptionRepository.findByIdOrNull(AnimeCaption.Key(animeNo, userAn))
            ?.run {
                animeCaptionRepository.delete(this)
                activePanelService.saveText("$userName 님이 ${anime?.subject} 자막을 종료하였습니다.")
                ResultStatus("OK")
            }
            ?: ResultStatus("FAIL", "이미 삭제되었습니다.")
}