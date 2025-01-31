package anissia.domain.session.repository

import anissia.domain.session.LoginToken
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

interface LoginTokenRepository : ReactiveCrudRepository<LoginToken, Long> {

    fun findByTokenNoAndTokenAndExpDtAfter(tokenNo: Long, token: String, expDt: OffsetDateTime): Mono<LoginToken>

    @Transactional
    @Modifying
    @Query("DELETE FROM LoginToken WHERE expDt < :expDt")
    fun deleteAllByExpDtBefore(expDt: OffsetDateTime = OffsetDateTime.now().minusDays(3)): Mono<Int>
}
