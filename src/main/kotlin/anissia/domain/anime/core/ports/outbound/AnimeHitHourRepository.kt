package anissia.domain.anime.core.ports.outbound

import anissia.domain.anime.core.AnimeHitHour
import anissia.domain.anime.core.model.AnimeRankItem
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface AnimeHitHourRepository : JpaRepository<AnimeHitHour, AnimeHitHour.Key> { //, QuerydslPredicateExecutor<AnimeHitHour> {

    @Modifying
    @Query("DELETE FROM AnimeHitHour WHERE hour < :hour")
    fun deleteByHourLessThan(hour: Long): Int

    @Query("""
        SELECT
            new anissia.domain.anime.core.model.AnimeRankItem(a.animeNo, (SELECT b.subject FROM Anime b WHERE b.animeNo = a.animeNo), sum(a.hit))
        FROM AnimeHitHour a
        WHERE a.hour >= :startHour
        GROUP BY a.animeNo ORDER BY sum(a.hit) DESC
    """)
    fun extractAllAnimeRank(startHour: Long, pageable: Pageable = PageRequest.of(0,100)): List<AnimeRankItem>

    @Modifying
    @Query("""
        insert into anime_hit_hour (hour, anime_no, hit)
        select ym, anime_no, sum(hit) hit from (select concat(left(hour, length(hour) - 2), '00') ym, anime_no, hit from anime_hit_hour
        where hour < cast(date_format(date_sub(curdate(), interval :beforeDays day), '%Y%m%d00') as unsigned) and hour % 100 != 0) a
        group by ym, anime_no on duplicate key update hit = hit + values(hit)
    """, nativeQuery = true)
    fun mergeByDay(beforeDays: Int): Int

    @Modifying
    @Query("delete from anime_hit_hour where hour < cast(date_format(date_sub(curdate(), interval :beforeDays day), '%Y%m%d00') as unsigned) and hour % 100 != 0", nativeQuery = true)
    fun deleteMergedByDay(beforeDays: Int): Int

    @Modifying
    @Query("""
        insert into anime_hit_hour (hour, anime_no, hit)
        select ym, anime_no, sum(hit) hit from (select concat(left(hour, length(hour) - 4), '0000') ym, anime_no, hit from anime_hit_hour
        where hour < cast(date_format(date_sub(curdate(), interval :beforeDays day), '%Y%m%d00') as unsigned) and hour % 10000 != 0) a
        group by ym, anime_no on duplicate key update hit = hit + values(hit)
    """, nativeQuery = true)
    fun mergeByMonth(beforeDays: Int): Int

    @Modifying
    @Query("delete from anime_hit_hour where hour < cast(date_format(date_sub(curdate(), interval :beforeDays day), '%Y%m%d00') as unsigned) and hour % 10000 != 0", nativeQuery = true)
    fun deleteMergedByMonth(beforeDays: Int): Int
}
