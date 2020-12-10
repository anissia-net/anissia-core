package anissia.repository

import anissia.domain.AnimeHitHour
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.transaction.annotation.Transactional

interface AnimeHitHourRepository : JpaRepository<AnimeHitHour, AnimeHitHour.Key>, QuerydslPredicateExecutor<AnimeHitHour> {
//    @Modifying
//    @Transactional
//    @Query("DELETE FROM AnimeHitHour WHERE dateHour < :dateHour")
//    fun deleteByDateHourLessThan(dateHour: String)
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
