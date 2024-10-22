package anissia.domain.account.repository

import anissia.domain.account.AccountRecoverAuth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

interface AccountRecoverAuthRepository : JpaRepository<AccountRecoverAuth, Long> { //, QuerydslPredicateExecutor<AccountRecoverAuth> {

    fun existsByAnAndExpDtAfter(an: Long, expDt: OffsetDateTime): Boolean

    fun findByNoAndTokenAndExpDtAfterAndUsedDtNull(no: Long, token: String, expDt: OffsetDateTime): AccountRecoverAuth?

    @Transactional
    @Modifying
    @Query("DELETE FROM AccountRecoverAuth WHERE expDt < :expDt")
    fun deleteAllByExpDtBefore(expDt: OffsetDateTime = OffsetDateTime.now().minusDays(30))
}
