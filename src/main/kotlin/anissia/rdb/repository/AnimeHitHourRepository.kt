package anissia.rdb.repository

import anissia.dto.AnimeRankDto
import anissia.rdb.entity.AnimeHitHour
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AnimeHitHourRepository : JpaRepository<AnimeHitHour, AnimeHitHour.Key>, QuerydslPredicateExecutor<AnimeHitHour> {

    @Modifying
    @Query("DELETE FROM AnimeHitHour WHERE hour < :hour")
    fun deleteByHourLessThan(hour: Long): Int

    @Query("""
        SELECT
            new anissia.dto.AnimeRankDto(a.animeNo, (SELECT b.subject FROM Anime b WHERE b.animeNo = a.animeNo), sum(a.hit))
        FROM AnimeHitHour a
        WHERE a.hour >= :startHour
        GROUP BY a.animeNo ORDER BY sum(a.hit) DESC
    """)
    fun extractAllAnimeRank(startHour: Long, pageable: Pageable = PageRequest.of(0,100)): List<AnimeRankDto>
}
