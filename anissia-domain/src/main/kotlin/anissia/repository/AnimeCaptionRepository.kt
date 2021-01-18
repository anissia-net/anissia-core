package anissia.repository

import anissia.domain.AnimeCaption
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AnimeCaptionRepository : JpaRepository<AnimeCaption, AnimeCaption.Key> {

    @EntityGraph(attributePaths = ["account"])
    fun findAllWithAccountByAnimeNoOrderByUpdDtDesc(animeNo: Long): List<AnimeCaption>

    @EntityGraph(attributePaths = ["anime"])
    @Query("SELECT a FROM AnimeCaption a JOIN a.anime b WHERE a.an = :an AND b.status <> anissia.domain.AnimeStatus.END ORDER BY a.updDt DESC")
    fun findAllWithAnimeForAdminCaptionActiveList(an: Long, pageable: Pageable): Page<AnimeCaption>

    @EntityGraph(attributePaths = ["anime"])
    @Query("SELECT a FROM AnimeCaption a JOIN a.anime b WHERE a.an = :an AND b.status = anissia.domain.AnimeStatus.END ORDER BY a.updDt DESC")
    fun findAllWithAnimeForAdminCaptionEndList(an: Long, pageable: Pageable): Page<AnimeCaption>

//
//    @EntityGraph(attributePaths = ["anime"])
//    fun findAllWithAnimeByUnOrderByUpdDtDesc(un: Long, pageable: Pageable): Page<AnimeCaption>
//
//    fun findByAnAndUn(an: Long, un: Long): AnimeCaption?
//
//    @EntityGraph(attributePaths = ["anime"])
//    fun findWithAnimeByAnAndUn(an: Long, un: Long): AnimeCaption?
}