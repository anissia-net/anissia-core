package anissia.domain.account.core.ports.outbound

import anissia.domain.account.core.AccountRecoverAuth
import org.springframework.data.jpa.repository.JpaRepository
import java.time.OffsetDateTime

interface AccountRecoverAuthRepository : JpaRepository<AccountRecoverAuth, Long> { //, QuerydslPredicateExecutor<AccountRecoverAuth> {

    fun existsByAnAndExpDtAfter(an: Long, expDt: OffsetDateTime): Boolean

    fun findByNoAndTokenAndExpDtAfterAndUsedDtNull(no: Long, token: String, expDt: OffsetDateTime): AccountRecoverAuth?
}
