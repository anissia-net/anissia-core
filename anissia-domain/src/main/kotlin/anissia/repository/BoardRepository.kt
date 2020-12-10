package anissia.repository

import anissia.domain.Board
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface BoardRepository : JpaRepository<Board, Long>, QuerydslPredicateExecutor<Board> {

//    @EntityGraph(attributePaths = ["user"])
//    fun findAllWithUserByCodeOrderByBnDesc(code: String, pageable: Pageable): Page<BoardTopic>
//
//    @EntityGraph(attributePaths = ["user"])
//    fun findAllWithUserBy(pageable: Pageable): List<BoardTopic>
//
//    @EntityGraph(attributePaths = ["user"])
//    fun findWithUserByBn(bn: Long): Optional<BoardTopic>
}
