package anissia.repository

import anissia.domain.UserLoginRememberToken
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface UserLoginRememberTokenRepository : JpaRepository<UserLoginRememberToken, Long> {
    //fun findByTnAndTokenAndExpDtAfter(tn: Long, token: String, expDt: LocalDateTime): UserLoginRememberToken?
}