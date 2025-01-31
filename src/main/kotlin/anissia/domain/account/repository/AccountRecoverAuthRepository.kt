package anissia.domain.account.repository

import anissia.domain.account.AccountRecoverAuth
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

interface AccountRecoverAuthRepository : ReactiveCrudRepository<AccountRecoverAuth, Long> {

    fun existsByAnAndExpDtAfter(an: Long, expDt: OffsetDateTime): Mono<Boolean>

    fun findByNoAndTokenAndExpDtAfterAndUsedDtNull(no: Long, token: String, expDt: OffsetDateTime): Mono<AccountRecoverAuth>

    @Transactional
    @Modifying
    @Query("DELETE FROM AccountRecoverAuth WHERE expDt < :expDt")
    fun deleteAllByExpDtBefore(expDt: OffsetDateTime = OffsetDateTime.now().minusDays(30)): Mono<Int>
}
