package anissia.repository

import anissia.domain.UserLoginPass
import org.springframework.data.jpa.repository.JpaRepository

interface UserLoginPassRepository : JpaRepository<UserLoginPass, Long>