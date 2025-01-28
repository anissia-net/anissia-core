package anissia.domain.session.repository

import anissia.domain.session.LoginFail
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

interface LoginFailRepository : ReactiveCrudRepository<LoginFail, Long> {

    fun countByIpAndEmailAndFailDtAfter(ip: String, email: String, failDt: OffsetDateTime): Mono<Long>

    @Modifying
    @Query("DELETE FROM LoginFail WHERE ip = :ip AND email = :email")
    fun deleteByIpAndEmail(ip: String, email: String)

    @Transactional
    @Modifying
    @Query("DELETE FROM LoginFail WHERE failDt < :failDt")
    fun deleteAllByFailDtBefore(failDt: OffsetDateTime = OffsetDateTime.now().minusDays(90))
}
