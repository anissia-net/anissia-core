package anissia.domain.session.core.ports.outbound

import anissia.domain.session.core.LoginToken
import org.springframework.data.jpa.repository.JpaRepository
import java.time.OffsetDateTime

interface LoginTokenRepository : JpaRepository<LoginToken, Long> {
    fun findByTokenNoAndTokenAndExpDtAfter(tokenNo: Long, token: String, expDt: OffsetDateTime): LoginToken?
}
