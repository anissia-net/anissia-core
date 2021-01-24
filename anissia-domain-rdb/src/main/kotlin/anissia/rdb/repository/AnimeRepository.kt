package anissia.rdb.repository

import anissia.rdb.domain.Anime
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AnimeRepository : JpaRepository<Anime, Long>, QuerydslPredicateExecutor<Anime> {

    @Query("SELECT A FROM Anime A WHERE A.status IN (anissia.rdb.domain.AnimeStatus.ON, anissia.rdb.domain.AnimeStatus.OFF) AND A.week = :week")
    fun findAllSchedule(week: String): List<Anime>

    @Query("SELECT A FROM Anime A WHERE A.status <> anissia.rdb.domain.AnimeStatus.DEL ORDER BY A.animeNo DESC")
    fun findAllByOrderByAnimeNoDesc(pageable: Pageable): Page<Anime>

    @Query("SELECT A FROM Anime A WHERE A.animeNo in :ids AND A.status <> anissia.rdb.domain.AnimeStatus.DEL ORDER BY A.animeNo DESC")
    fun findAllByIdInOrderByAnimeNoDesc(ids: List<Long>): List<Anime>

    @Query("SELECT A FROM Anime A WHERE A.status = anissia.rdb.domain.AnimeStatus.DEL ORDER BY A.animeNo DESC")
    fun findAllDelByOrderByAnimeNoDesc(pageable: Pageable): Page<Anime>

    fun findTop5ByAutocorrectStartsWithOrderByAutocorrect(autocorrect: String): List<Anime>

    @EntityGraph(attributePaths = ["captions"])
    fun findWithCaptionsByAnimeNo(animeNo: Long): Anime?
}
