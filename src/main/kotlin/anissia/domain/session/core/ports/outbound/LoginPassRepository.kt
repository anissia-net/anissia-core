package anissia.domain.session.core.ports.outbound

import anissia.domain.session.core.LoginPass
import org.springframework.data.jpa.repository.JpaRepository

interface LoginPassRepository : JpaRepository<LoginPass, Long>
