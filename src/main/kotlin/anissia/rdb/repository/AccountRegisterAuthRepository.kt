package anissia.rdb.repository

import anissia.rdb.entity.AccountRegisterAuth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.time.LocalDateTime

interface AccountRegisterAuthRepository : JpaRepository<AccountRegisterAuth, Long>, QuerydslPredicateExecutor<AccountRegisterAuth> {

    fun existsByEmailAndExpDtAfter(email: String, expDt: LocalDateTime): Boolean

    fun findByNoAndTokenAndExpDtAfterAndUsedDtNull(no: Long, token: String, expDt: LocalDateTime): AccountRegisterAuth?
}
