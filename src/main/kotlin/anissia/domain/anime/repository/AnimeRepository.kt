package anissia.domain.anime.repository

import anissia.domain.anime.Anime
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface AnimeRepository : JpaRepository<Anime, Long> { //, QuerydslPredicateExecutor<Anime> {

    @Query("SELECT A FROM Anime A WHERE A.status <> anissia.domain.anime.AnimeStatus.END AND A.week = :week")
    fun findAllSchedule(week: String): List<Anime>

    fun findAllByOrderByAnimeNoDesc(pageable: Pageable): Page<Anime>

    fun findAllByAnimeNoInOrderByAnimeNoDesc(animeNo: Collection<Long>): List<Anime>

    fun existsBySubject(subject: String): Boolean

    fun existsBySubjectAndAnimeNoNot(subject: String, animeNo: Long): Boolean

    @Query("SELECT A FROM Anime A WHERE A.animeNo IN :animeNo")
    fun findAllByIds(animeNo: List<Long>): List<Anime>

    @EntityGraph(attributePaths = ["captions"])
    fun findWithCaptionsByAnimeNo(animeNo: Long): Anime?

    @Modifying
    @Query("UPDATE Anime A SET A.captionCount = size(A.captions) WHERE A.animeNo = :animeNo")
    fun updateCaptionCount(animeNo: Long): Int

    @Modifying
    @Query("UPDATE Anime A SET A.captionCount = size(A.captions) WHERE A.animeNo IN :animeNo")
    fun updateCaptionCountByIds(animeNo: List<Long>): Int

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("UPDATE Anime A SET A.captionCount = size(A.captions)")
    fun updateCaptionCountAll(): Int

    @Query("SELECT concat(a.anime_no, ' ', a.subject) FROM anime a WHERE a.autocorrect LIKE concat(:autocorrect, '%')", nativeQuery = true)
    fun findTop10ByAutocorrectStartsWith(autocorrect: String, pageable: Pageable = PageRequest.of(0, 10)): List<String>
}
