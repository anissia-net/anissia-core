package anissia.domain.anime.repository

import anissia.domain.anime.AnimeHit
import anissia.domain.anime.AnimeHitHour
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AnimeHitRepository : ReactiveCrudRepository<AnimeHit, Long> {
    @Modifying
    @Query("DELETE FROM AnimeHit WHERE hour < :hour")
    fun deleteByHourLessThan(hour: Long): Mono<Int>

    @Query(
        """
        SELECT
            new anissia.domain.anime.AnimeHitHour(a.hour, a.animeNo, count(distinct a.ip))
        FROM AnimeHit a
        WHERE a.hour < :hour
        GROUP BY a.hour, a.animeNo
    """
    )
    fun extractAllAnimeHitHour(hour: Long): Flux<AnimeHitHour>
}
