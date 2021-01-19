package anissia.rdb.repository

import anissia.rdb.domain.AccountRecoverAuth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AccountRecoverAuthRepository : JpaRepository<AccountRecoverAuth, Long>, QuerydslPredicateExecutor<AccountRecoverAuth> {
//
//    fun existsByUnAndExpDtAfter(un: Long, expDt: LocalDateTime): Boolean
//
//    fun findByNoAndTokenAndExpDtAfterAndUsedDtNull(no: Long, token: String, expDt: LocalDateTime): UserRecoverAuth?
}
