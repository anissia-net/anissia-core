package anissia.services

import anissia.domain.AnimeHit
import anissia.dto.AnimeCaptionDto
import anissia.dto.AnimeScheduleDto
import anissia.misc.As
import anissia.repository.AnimeCaptionRepository
import anissia.repository.AnimeHitRepository
import anissia.repository.AnimeRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@Service
class AnimeService(
    private val animeRepository: AnimeRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeHitRepository: AnimeHitRepository,
    private val request: HttpServletRequest,
    private val asyncService: AsyncService
) {

    fun getCaptionByAnimeNo(animeNo: Long): List<AnimeCaptionDto> = animeCaptionRepository
        .findAllWithAccountByAnimeNoOrderByUpdDtDesc(animeNo)
        .map { AnimeCaptionDto(it) }
        .also { // add anime hit
            val ip = request.remoteAddr
            asyncService.async { animeHitRepository.save(AnimeHit(animeNo = animeNo, ip = ip, hour = LocalDateTime.now().format(As.DTF_RANK_HOUR))) }
        }

    fun getSchedule(week: String): List<AnimeScheduleDto> = animeRepository
        .findAllSchedule(week)
        .map { AnimeScheduleDto(it) }
        .run {
            when(week) {
                "7" -> sortedByDescending { if (it.time != "") it.time else "9999" }
                "8" -> sortedBy { if (it.time != "") it.time else "9999" }
                else -> sortedBy { it.time }
            }
        }

}