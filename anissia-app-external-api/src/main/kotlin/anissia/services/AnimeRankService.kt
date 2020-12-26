package anissia.services

import anissia.misc.As
import anissia.repository.AnimeHitHourRepository
import anissia.repository.AnimeHitRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AnimeRankService(
    private val animeHitRepository: AnimeHitRepository,
    private val animeHitHourRepository: AnimeHitHourRepository,
) {

    fun mergeAnimeHit() {
        val hour = LocalDateTime.now().format(As.DTF_RANK_HOUR)
        animeHitRepository.extractAllAnimeHitHour(hour)
            .map { r -> animeHitHourRepository.findById(r.key).map { it.hit += r.hit; it }.orElse(r) }
            .also { animeHitHourRepository.saveAll(it) }
    }

    fun extractRank() {
        println("랭크")
        animeHitHourRepository.extractAllAnimeRank(LocalDateTime.now().minusHours(1).format(As.DTF_RANK_HOUR)).forEach { println(it) }
    }

}