package anissia.repository

import anissia.domain.AnimeCaption
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface AnimeCaptionRepository : JpaRepository<AnimeCaption, AnimeCaption.Key> {

    @EntityGraph(attributePaths = ["account"])
    fun findAllWithAccountByAnimeNoOrderByUpdDtDesc(animeNo: Long): List<AnimeCaption>

//
//    @EntityGraph(attributePaths = ["anime"])
//    fun findAllWithAnimeByUnOrderByUpdDtDesc(un: Long, pageable: Pageable): Page<AnimeCaption>
//
//    fun findByAnAndUn(an: Long, un: Long): AnimeCaption?
//
//    @EntityGraph(attributePaths = ["anime"])
//    fun findWithAnimeByAnAndUn(an: Long, un: Long): AnimeCaption?
}