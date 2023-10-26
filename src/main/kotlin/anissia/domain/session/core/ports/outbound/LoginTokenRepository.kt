package anissia.domain.session.core.ports.outbound

import anissia.domain.session.core.LoginToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

interface LoginTokenRepository : JpaRepository<LoginToken, Long> {
    fun findByTokenNoAndTokenAndExpDtAfter(tokenNo: Long, token: String, expDt: OffsetDateTime): LoginToken?

    @Transactional
    @Modifying
    @Query("DELETE FROM LoginToken WHERE expDt < :expDt")
    fun deleteAllByExpDtBefore(expDt: OffsetDateTime = OffsetDateTime.now().minusDays(3))
}
