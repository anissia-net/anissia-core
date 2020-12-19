package anissia.repository

import anissia.domain.Anime
import anissia.domain.AnimeStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AnimeRepository : JpaRepository<Anime, Long>, QuerydslPredicateExecutor<Anime> {

    @Query("SELECT A FROM Anime A WHERE A.status <> :expectStatus AND A.week = :week")
    fun findAllSchedule(week: String, expectStatus: AnimeStatus = AnimeStatus.END): List<Anime>


    //fun findByAn(an: Long): Anime?
}
//
//interface AnimeRepositoryCustom {
//    fun findAllTimetableAnime(bcType: String): List<AnimeTimetableModel>
//}
//
//class AnimeRepositoryCustomImpl(private val entityManager: EntityManager) : AnimeRepositoryCustom {
//    override fun findAllTimetableAnime(bcType: String): List<AnimeTimetableModel> {
//        val anime = QAnime.anime
//        val bcTimeOrDate = Expressions.stringPath("bcTimeOrDate")
//        return JPAQuery<Anime>(entityManager)
//                .select(Projections.constructor(
//                        AnimeTimetableModel::class.java,
//                        anime.bcType,
//                        anime.an,
//                        anime.ongoing,
//                        Expressions.cases().`when`(anime.bcType.notIn("7", "8"))
//                                .then(anime.bcTime)
//                                .otherwise(anime.startDate).`as`(bcTimeOrDate.toString()),
//                        anime.subject,
//                        anime.genres,
//                        anime.startDate,
//                        anime.endDate,
//                        anime.website
//                )).from(anime)
//                .where(anime.status.eq(0).and(anime.bcType.eq(bcType)))
//                .orderBy(if (bcType == "7") bcTimeOrDate.desc() else bcTimeOrDate.asc())
//                .fetch()
//    }
//}
