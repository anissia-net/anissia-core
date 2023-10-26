package anissia.domain.session.core.ports.outbound

import anissia.domain.session.core.LoginFail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

interface LoginFailRepository : JpaRepository<LoginFail, Long> {

    fun countByIpAndEmailAndFailDtAfter(ip: String, email: String, failDt: OffsetDateTime): Long

    @Modifying
    @Query("DELETE FROM LoginFail WHERE ip = :ip AND email = :email")
    fun deleteByIpAndEmail(ip: String, email: String)

    @Transactional
    @Modifying
    @Query("DELETE FROM LoginFail WHERE failDt < :failDt")
    fun deleteAllByFailDtBefore(failDt: OffsetDateTime = OffsetDateTime.now().minusDays(90))
}
