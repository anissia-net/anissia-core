package anissia.domain.session.core.ports.outbound

import anissia.domain.session.core.LoginPass
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

interface LoginPassRepository : JpaRepository<LoginPass, Long> {


    @Transactional
    @Modifying
    @Query("DELETE FROM LoginPass WHERE passDt < :passDt")
    fun deleteAllByPassDtBefore(passDt: OffsetDateTime = OffsetDateTime.now().minusDays(90))
}
