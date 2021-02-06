package anissia.rdb.repository

import anissia.rdb.domain.Anime
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AnimeRepository : JpaRepository<Anime, Long>, QuerydslPredicateExecutor<Anime> {

    @Query("SELECT A FROM Anime A WHERE A.status <> anissia.rdb.domain.AnimeStatus.END AND A.week = :week")
    fun findAllSchedule(week: String): List<Anime>

    fun findAllByOrderByAnimeNoDesc(pageable: Pageable): Page<Anime>

    fun findAllByAnimeNoInOrderByAnimeNoDesc(animeNo: Collection<Long>): List<Anime>

    @EntityGraph(attributePaths = ["captions"])
    fun findWithCaptionsByAnimeNo(animeNo: Long): Anime?

    @Modifying
    @Query("UPDATE Anime A SET A.captionCount = A.captions.size WHERE A.animeNo = :animeNo")
    fun updateCaptionCount(animeNo: Long): Int

    @Modifying
    @Query("UPDATE Anime A SET A.captionCount = A.captions.size")
    fun updateAllCaptionCount(): Int

    /**
     * use nativeQuery for the performance and system resource
     * fun findTop10ByAutocorrectStartsWithOrderBySubject(autocorrect: String): List<Anime>
     */
    @Query("SELECT concat(anime_no, ' ', subject) FROM anime WHERE autocorrect LIKE concat(:autocorrect, '%') LIMIT 10", nativeQuery = true)
    fun findTop10ByAutocorrectStartsWith(autocorrect: String): List<String>
}
