package anissia.rdb.repository

import anissia.rdb.entity.BoardTicker
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface BoardTickerRepository : JpaRepository<BoardTicker, String>, QuerydslPredicateExecutor<BoardTicker> {

//    @EntityGraph(attributePaths = ["user"])
//    fun findAllWithUserByCodeOrderByBnDesc(code: String, pageable: Pageable): Page<BoardTopic>
//
//    @EntityGraph(attributePaths = ["user"])
//    fun findAllWithUserBy(pageable: Pageable): List<BoardTopic>
//
//    @EntityGraph(attributePaths = ["user"])
//    fun findWithUserByBn(bn: Long): Optional<BoardTopic>
}
