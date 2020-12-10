package anissia.repository

import anissia.domain.UserRecoverAuth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.time.LocalDateTime

interface UserRecoverAuthRepository : JpaRepository<UserRecoverAuth, Long>, QuerydslPredicateExecutor<UserRecoverAuth> {
//
//    fun existsByUnAndExpDtAfter(un: Long, expDt: LocalDateTime): Boolean
//
//    fun findByNoAndTokenAndExpDtAfterAndUsedDtNull(no: Long, token: String, expDt: LocalDateTime): UserRecoverAuth?
}
