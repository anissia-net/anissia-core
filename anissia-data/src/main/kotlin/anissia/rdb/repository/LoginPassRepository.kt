package anissia.rdb.repository

import anissia.rdb.entity.LoginPass
import org.springframework.data.jpa.repository.JpaRepository

interface LoginPassRepository : JpaRepository<LoginPass, Long>