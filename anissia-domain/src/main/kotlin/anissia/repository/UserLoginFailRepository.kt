package anissia.repository

import anissia.domain.UserLoginFail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import javax.transaction.Transactional

interface UserLoginFailRepository : JpaRepository<UserLoginFail, Long> {
//
//    fun countByIpAndAccountAndFailDtAfter(ip: String, account: String, failDt: LocalDateTime): Long
//
//    @Transactional
//    @Modifying
//    @Query("DELETE FROM UserLoginFail WHERE ip = :ip AND account = :account")
//    fun deleteByIpAndAccount(ip: String, account: String)
}