package anissia.repository

import anissia.domain.BoardTopic
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.util.*

interface BoardTopicRepository : JpaRepository<BoardTopic, Long>, QuerydslPredicateExecutor<BoardTopic> {

    @EntityGraph(attributePaths = ["user"])
    fun findAllWithUserByCodeOrderByBnDesc(code: String, pageable: Pageable): Page<BoardTopic>

    @EntityGraph(attributePaths = ["user"])
    fun findAllWithUserBy(pageable: Pageable): List<BoardTopic>

    @EntityGraph(attributePaths = ["user"])
    fun findWithUserByBn(bn: Long): Optional<BoardTopic>
}
