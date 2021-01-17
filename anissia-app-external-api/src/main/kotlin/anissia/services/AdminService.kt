package anissia.services

import anissia.dto.AdminAnimeCaptionDto
import anissia.repository.AnimeCaptionRepository
import anissia.repository.AnimeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
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

    fun getCaptionList(active: Int, page: Int): Page<AdminAnimeCaptionDto> =
        animeCaptionRepository
            .findAllWithAnimeForAdminCaptionActiveList(an, PageRequest.of(page, 20))
            .map { AdminAnimeCaptionDto(it) }


}