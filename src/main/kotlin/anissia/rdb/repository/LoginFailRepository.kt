package anissia.rdb.repository

import anissia.rdb.entity.LoginFail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface LoginFailRepository : JpaRepository<LoginFail, Long> {

    fun countByIpAndEmailAndFailDtAfter(ip: String, email: String, failDt: LocalDateTime): Long

    @Modifying
    @Query("DELETE FROM LoginFail WHERE ip = :ip AND email = :email")
    fun deleteByIpAndEmail(ip: String, email: String)
}