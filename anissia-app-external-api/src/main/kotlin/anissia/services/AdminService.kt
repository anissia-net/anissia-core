package anissia.services

import anissia.misc.As
import anissia.rdb.domain.AnimeCaption
import anissia.rdb.dto.AdminAnimeCaptionDto
import anissia.rdb.dto.ResultStatus
import anissia.rdb.dto.request.AdminAnimeCaptionRequest
import anissia.rdb.repository.AnimeCaptionRepository
import anissia.rdb.repository.AnimeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@Service
class AdminService(
    private val animeRepository: AnimeRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val request: HttpServletRequest,
    private val sessionService: SessionService,
    private val activePanelService: ActivePanelService
) {

    val user get() = sessionService.session
    val userAn get() = user?.an ?: 0
    val userName get() = user?.name ?: 0

    fun getCaptionList(active: Int, page: Int): Page<AdminAnimeCaptionDto> = (
            if (active == 1) animeCaptionRepository.findAllWithAnimeForAdminCaptionActiveList(userAn, PageRequest.of(page, 20))
            else animeCaptionRepository.findAllWithAnimeForAdminCaptionEndList(userAn, PageRequest.of(page, 20))
            ).map { AdminAnimeCaptionDto(it) }


    fun addCaption(animeNo: Long) = editCaption(animeNo, AdminAnimeCaptionRequest(), true)

    fun editCaption(animeNo: Long, caption: AdminAnimeCaptionRequest, isNew: Boolean = false): ResultStatus {
        caption.validate()

        if (isNew) {
            if (animeCaptionRepository.findById(AnimeCaption.Key(animeNo, userAn)).isPresent) {
                return ResultStatus("FAIL", "이미 작업중인 작품입니다.")
            }
        } else {
            if (animeCaptionRepository.findById(AnimeCaption.Key(animeNo, userAn)).isEmpty) {
                return ResultStatus("FAIL", "존재하지 않는 자막입니다.")
            }
        }

        val anime = animeRepository.findByIdOrNull(animeNo)
            ?: return ResultStatus("FAIL", "존재하지 않는 애니메이션입니다.")

        animeCaptionRepository.save(AnimeCaption(
            animeNo = animeNo,
            an = userAn,
            episode = caption.episode,
            updDt = caption.updDt,
            website = caption.website,
        ))

        if (isNew) {
            activePanelService.saveText("$userName 님이 ${anime.subject} 자막을 시작하였습니다.")
        }

        return ResultStatus("OK")
    }

    fun delCaption(animeNo: Long) =
        animeCaptionRepository.findByIdOrNull(AnimeCaption.Key(animeNo, userAn))
            ?.run {
                animeCaptionRepository.delete(this)
                activePanelService.saveText("$userName 님이 ${anime?.subject} 자막을 종료하였습니다.")
                ResultStatus("OK")
            }
            ?: ResultStatus("FAIL", "이미 삭제되었습니다.")
}