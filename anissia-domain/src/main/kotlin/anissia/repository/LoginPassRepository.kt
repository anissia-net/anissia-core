package anissia.repository

import anissia.domain.LoginPass
import org.springframework.data.jpa.repository.JpaRepository

interface LoginPassRepository : JpaRepository<LoginPass, Long>