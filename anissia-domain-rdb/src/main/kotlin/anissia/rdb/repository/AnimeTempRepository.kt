package anissia.rdb.repository

import anissia.rdb.domain.Anime
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AnimeTempRepository : JpaRepository<Anime, Long>, QuerydslPredicateExecutor<Anime> {

    @Query("SELECT A FROM Anime A WHERE A.status = anissia.rdb.domain.AnimeStatus.DEL ORDER BY A.animeNo DESC")
    fun findAllDelByOrderByAnimeNoDesc(pageable: Pageable): Page<Anime>

    @EntityGraph(attributePaths = ["captions"])
    fun findWithCaptionsByAnimeNo(animeNo: Long): Anime?
}
