package anissia.domain.board.repository

import anissia.domain.board.BoardTopic
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface BoardTopicRepository : ReactiveCrudRepository<BoardTopic, Long> {

    @EntityGraph(attributePaths = ["account"])
    fun findWithAccountByTickerAndTopicNo(ticker: String, topicNo: Long): Mono<BoardTopic>

    @EntityGraph(attributePaths = ["account"])
    fun findAllWithAccountByTickerOrderByTickerAscFixedDescTopicNoDesc(ticker: String, pageable: Pageable): Mono<Page<BoardTopic>>

    fun findTop5ByTickerAndFixedOrderByTopicNoDesc(ticker: String, fixed: Boolean = false): Flux<BoardTopic>

    @Modifying
    @Query("UPDATE BoardTopic A SET A.postCount = size(A.posts) - 1 WHERE A.topicNo = :topicNo")
    fun updatePostCount(topicNo: Long): Mono<Int>
}
