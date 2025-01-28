package anissia.domain.board.repository

import anissia.domain.board.BoardPost
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface BoardPostRepository : ReactiveCrudRepository<BoardPost, Long> {

    @EntityGraph(attributePaths = ["account"])
    fun findAllWithAccountByTopicNoOrderByPostNo(topicNo: Long): Flux<BoardPost>

    @EntityGraph(attributePaths = ["account"])
    fun findWithAccountByTopicNoAndRootIsTrue(topicNo: Long): Mono<BoardPost>

    @Modifying
    @Query("DELETE FROM BoardPost A WHERE A.topicNo = :topicNo")
    fun deleteAllByTopicNo(topicNo: Long): Mono<Int>
}
