package anissia.services

import anissia.domain.AnimeCaption
import anissia.dto.AdminAnimeCaptionDto
import anissia.dto.ResultStatus
import anissia.misc.As
import anissia.repository.AnimeCaptionRepository
import anissia.repository.AnimeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest

@Service
class AdminService(
    private val animeRepository: AnimeRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeRankService: AnimeRankService,
    private val request: HttpServletRequest,
    private val sessionService: SessionService
) {

    val session get() = sessionService.session
    val an get() = session?.an ?: 0

    fun getCaptionList(active: Int, page: Int): Page<AdminAnimeCaptionDto> = (
            if (active == 1) animeCaptionRepository.findAllWithAnimeForAdminCaptionActiveList(an, PageRequest.of(page, 20))
            else animeCaptionRepository.findAllWithAnimeForAdminCaptionEndList(an, PageRequest.of(page, 20))
            ).map { AdminAnimeCaptionDto(it) }

    fun addCaption(caption: AdminAnimeCaptionDto): ResultStatus {
        if (animeCaptionRepository.findById(AnimeCaption.Key(caption.animeNo, an)).isPresent) {
            return ResultStatus("FAIL", "이미 작업중인 작품입니다.")
        }

        animeCaptionRepository.save(AnimeCaption(
            animeNo = caption.animeNo,
            an = an,
            episode = caption.episode,
            updDt = LocalDateTime.parse(caption.updDt, As.DTF_CAPTION),
            website = caption.website,
        ))

        return ResultStatus("OK")
    }
}