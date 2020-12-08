package anissia.repository

import anissia.domain.AnimeCaption
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AnimeCaptionRepository : JpaRepository<AnimeCaption, AnimeCaption.Key>, QuerydslPredicateExecutor<AnimeCaption> {
    @EntityGraph(attributePaths = ["user"])
    fun findAllWithUserByAnOrderByUpdDtDesc(an: Long?): List<AnimeCaption>

    @EntityGraph(attributePaths = ["anime"])
    fun findAllWithAnimeByUnOrderByUpdDtDesc(un: Long, pageable: Pageable): Page<AnimeCaption>

    fun findByAnAndUn(an: Long, un: Long): AnimeCaption?

    @EntityGraph(attributePaths = ["anime"])
    fun findWithAnimeByAnAndUn(an: Long, un: Long): AnimeCaption?
}