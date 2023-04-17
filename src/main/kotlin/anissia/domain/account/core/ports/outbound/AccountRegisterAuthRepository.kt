package anissia.domain.account.core.ports.outbound

import anissia.domain.account.core.AccountRegisterAuth
import org.springframework.data.jpa.repository.JpaRepository
import java.time.OffsetDateTime

interface AccountRegisterAuthRepository : JpaRepository<AccountRegisterAuth, Long> { //, QuerydslPredicateExecutor<AccountRegisterAuth> {

    fun existsByEmailAndExpDtAfter(email: String, expDt: OffsetDateTime): Boolean

    fun findByNoAndTokenAndExpDtAfterAndUsedDtNull(no: Long, token: String, expDt: OffsetDateTime): AccountRegisterAuth?
}
