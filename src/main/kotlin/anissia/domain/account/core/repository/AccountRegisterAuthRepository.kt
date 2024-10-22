package anissia.domain.account.core.repository

import anissia.domain.account.core.AccountRegisterAuth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

interface AccountRegisterAuthRepository : JpaRepository<AccountRegisterAuth, Long> { //, QuerydslPredicateExecutor<AccountRegisterAuth> {

    fun existsByEmailAndExpDtAfter(email: String, expDt: OffsetDateTime): Boolean

    fun findByNoAndTokenAndExpDtAfterAndUsedDtNull(no: Long, token: String, expDt: OffsetDateTime): AccountRegisterAuth?

    @Transactional
    @Modifying
    @Query("DELETE FROM AccountRegisterAuth WHERE expDt < :expDt")
    fun deleteAllByExpDtBefore(expDt: OffsetDateTime = OffsetDateTime.now().minusDays(30))
}
