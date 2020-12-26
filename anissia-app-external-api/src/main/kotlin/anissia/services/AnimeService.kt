package anissia.services

import anissia.dto.AnimeCaptionDto
import anissia.dto.AnimeScheduleDto
import anissia.repository.AnimeCaptionRepository
import anissia.repository.AnimeRepository
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class AnimeService(
    private val animeRepository: AnimeRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeRankService: AnimeRankService,
    private val request: HttpServletRequest
) {

    fun getCaptionByAnimeNo(animeNo: Long): List<AnimeCaptionDto> =
        animeCaptionRepository
            .findAllWithAccountByAnimeNoOrderByUpdDtDesc(animeNo)
            .map { AnimeCaptionDto(it) }
            .also { animeRankService.hitAsync(animeNo, request.remoteAddr) }

    fun getSchedule(week: String): List<AnimeScheduleDto> =
        animeRepository
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