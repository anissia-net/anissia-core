package anissia.repository

import anissia.domain.AccountRegisterAuth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AccountRegisterAuthRepository : JpaRepository<AccountRegisterAuth, Long>, QuerydslPredicateExecutor<AccountRegisterAuth> {
//
//    fun existsByEmailAndExpDtAfter(email: String, expDt: LocalDateTime): Boolean
//
//    fun findByNoAndTokenAndExpDtAfterAndUsedDtNull(no: Long, token: String, expDt: LocalDateTime): UserRegisterAuth?
}
