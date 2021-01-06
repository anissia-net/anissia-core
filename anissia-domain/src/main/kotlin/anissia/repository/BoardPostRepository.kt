package anissia.repository

import anissia.domain.BoardPost
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface BoardPostRepository : JpaRepository<BoardPost, Long>, QuerydslPredicateExecutor<BoardPost> {


    @EntityGraph(attributePaths = ["account"])
    fun findAllWithAccountByTopicNoOrderByPostNo(topicNo: Long): List<BoardPost>


//    @EntityGraph(attributePaths = ["user"])
//    fun findAllWithUserByCodeOrderByBnDesc(code: String, pageable: Pageable): Page<BoardTopic>
//
//    @EntityGraph(attributePaths = ["user"])
//    fun findAllWithUserBy(pageable: Pageable): List<BoardTopic>
//
//    @EntityGraph(attributePaths = ["user"])
//    fun findWithUserByBn(bn: Long): Optional<BoardTopic>
}
