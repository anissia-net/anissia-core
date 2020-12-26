package anissia.repository

import anissia.domain.AnimeHitHour
import anissia.domain.AnimeRankDto
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AnimeHitHourRepository : JpaRepository<AnimeHitHour, AnimeHitHour.Key>, QuerydslPredicateExecutor<AnimeHitHour> {

    @Query("""
        SELECT
            new anissia.domain.AnimeRankDto(a.animeNo, (SELECT b.subject FROM Anime b WHERE b.animeNo = a.animeNo), sum(a.hit))
        FROM AnimeHitHour a
        WHERE a.hour >= :startHour
        AND (:endHour is null or a.hour < :endHour)
        GROUP BY a.animeNo ORDER BY sum(a.hit) DESC
    """)
    fun extractAllAnimeRank(startHour: String, endHour: String? = null, pageable: Pageable = PageRequest.of(0,100)): List<AnimeRankDto>

    @Modifying
    @Query("DELETE FROM AnimeHitHour WHERE hour < :hour")
    fun deleteByHourLessThan(hour: String): Long
}

//interface AnimeHitHourRepositoryCustom {
//    fun findAllTop30GroupByAnOrderByHitDesc(dateHour: String): List<AnimeHitRankModel>
//}
//
//class AnimeHitHourRepositoryImpl(private var entityManager: EntityManager) : AnimeHitHourRepositoryCustom {
//
//    override fun findAllTop30GroupByAnOrderByHitDesc(dateHour: String): List<AnimeHitRankModel> {
//        val animeHitHour = QAnimeHitHour.animeHitHour
//        return JPAQuery<AnimeHitHour>(entityManager)
//                .select(Projections.constructor(
//                        AnimeHitRankModel::class.java,
//                        animeHitHour.an,
//                        Expressions.asNumber(0), // ranking
//                        Expressions.asNumber(0), // change
//                        animeHitHour.anime.subject,
//                        animeHitHour.anime.genres,
//                        animeHitHour.hit.sum()
//                )).from(animeHitHour)
//                .where(animeHitHour.dateHour.goe(dateHour))
//                .join(animeHitHour.anime, QAnime.anime)
//                .groupBy(animeHitHour.an)
//                .orderBy(animeHitHour.hit.sum().desc())
//                .limit(30)
//                .fetch()
//    }
//}
