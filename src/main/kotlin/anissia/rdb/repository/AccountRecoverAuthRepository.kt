package anissia.rdb.repository

import anissia.rdb.entity.AccountRecoverAuth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.time.LocalDateTime

interface AccountRecoverAuthRepository : JpaRepository<AccountRecoverAuth, Long>, QuerydslPredicateExecutor<AccountRecoverAuth> {

    fun existsByAnAndExpDtAfter(an: Long, expDt: LocalDateTime): Boolean

    fun findByNoAndTokenAndExpDtAfterAndUsedDtNull(no: Long, token: String, expDt: LocalDateTime): AccountRecoverAuth?
}
