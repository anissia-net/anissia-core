package anissia.rdb.repository

import anissia.rdb.domain.LoginToken
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface LoginTokenRepository : JpaRepository<LoginToken, Long> {
    fun findByTokenNoAndTokenAndExpDtAfter(tokenNo: Long, token: String, expDt: LocalDateTime): LoginToken?
}