package anissia.services

import anissia.dto.AnimeSchedule
import anissia.repository.AnimeRepository
import org.springframework.stereotype.Service

@Service
class AnimeService(
    private val animeRepository: AnimeRepository
) {

    fun getSchedule(week: String) = animeRepository.findAllSchedule(week).map { AnimeSchedule(it) }.run {
            when(week) {
                "7" -> sortedByDescending { if (it.time != "") it.time else "9999" }
                "8" -> sortedBy { if (it.time != "") it.time else "9999" }
                else -> sortedBy { it.time }
            }
        }
}