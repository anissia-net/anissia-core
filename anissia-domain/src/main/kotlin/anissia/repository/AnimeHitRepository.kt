package anissia.repository

import anissia.domain.AnimeHit
import anissia.domain.AnimeHitHour
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AnimeHitRepository : JpaRepository<AnimeHit, Long>, QuerydslPredicateExecutor<AnimeHit> {
    @Modifying
    @Query("DELETE FROM AnimeHit WHERE hour < :hour")
    fun deleteByHourLessThan(hour: String): Int

    @Query(
        """
        SELECT
            new anissia.domain.AnimeHitHour(a.hour, a.animeNo, count(distinct a.ip))
        FROM AnimeHit a
        WHERE a.hour < :hour
        GROUP BY a.hour, a.animeNo
    """
    )
    fun extractAllAnimeHitHour(hour: String): List<AnimeHitHour>
}