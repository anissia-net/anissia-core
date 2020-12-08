package anissia.repository

import anissia.domain.UserRegisterAuth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.time.LocalDateTime

interface UserRegisterAuthRepository : JpaRepository<UserRegisterAuth, Long>, QuerydslPredicateExecutor<UserRegisterAuth> {

    fun existsByEmailAndExpDtAfter(email: String, expDt: LocalDateTime): Boolean

    fun findByNoAndTokenAndExpDtAfterAndUsedDtNull(no: Long, token: String, expDt: LocalDateTime): UserRegisterAuth?
}
