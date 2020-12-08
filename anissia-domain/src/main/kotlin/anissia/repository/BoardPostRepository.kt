package anissia.repository

import anissia.domain.BoardPost
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface BoardPostRepository : JpaRepository<BoardPost, Long>, QuerydslPredicateExecutor<BoardPost> {
    @EntityGraph(attributePaths = ["user"])
    fun findAllWithUserByBnOrderByPnAsc(bn: Long): List<BoardPost>
}
