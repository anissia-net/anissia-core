package anissia.services

import anissia.configruration.logger
import anissia.dto.ResultData
import anissia.dto.ResultStatus
import anissia.dto.request.AccountRecoverPasswordRequest
import anissia.dto.request.AccountRecoverRequest
import anissia.dto.request.EmailAuthTokenRequest
import anissia.misc.As
import anissia.rdb.entity.Account
import anissia.rdb.entity.AccountRecoverAuth
import anissia.rdb.repository.AccountRecoverAuthRepository
import anissia.rdb.repository.AccountRepository
import me.saro.kit.Texts
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest

@Service
class AccountRecoverService(
    private val accountRepository: AccountRepository,
    private val accountRecoverAuthRepository: AccountRecoverAuthRepository,
    private val passwordEncoder: PasswordEncoder,
    private val asyncService: AsyncService,
    private val emailService: EmailService,
    private val request: HttpServletRequest,
    @Value("\${host}") private val host: String
) {

    private val log = logger<AccountRecoverService>()
    private val recoverAuthHtml = As.getResource("/email/account-recover-auth.html").readText()
    private val emailDateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")

    companion object {
        private const val EXP_HOUR = 1L
    }

    @Transactional
    fun recover(recover: AccountRecoverRequest): ResultStatus {
        var account: Account = accountRepository.findByEmailAndName(recover.email, recover.name)
            // not exist match info is secret for protect personal information
            // 개인정보보호를 위해 일치 하지 않는경우에도 일치하는 것과 같은 정보를 내보낸다.
            ?: return ResultStatus("OK")

        if (accountRecoverAuthRepository.existsByAnAndExpDtAfter(account.an, LocalDateTime.now())) {
            return ResultStatus("FAIL", "인증을 시도한 계정은 ${EXP_HOUR}시간동안 인증을 할 수 없습니다.")
        }

        val ip = request.remoteAddr

        val auth = accountRecoverAuthRepository
            .save(AccountRecoverAuth(
                token = Texts.createRandomBase62String(128, 256),
                an = account.an,
                ip = ip,
                expDt = LocalDateTime.now().plusHours(EXP_HOUR))
            )

        asyncService.async {
            emailService.send(
                recover.email,
                "[애니시아] 계정 복원 이메일 인증",
                recoverAuthHtml
                    .replace("[[ip]]", ip)
                    .replace("[[exp_dt]]", emailDateFormat.format(auth.expDt))
                    .replace("[[url]]", "${host}/recover/${auth.no}-${auth.token}")
            )
        }

        return ResultStatus("OK")
    }

    fun recoverValidation(token: EmailAuthTokenRequest): ResultStatus =
        accountRecoverAuthRepository.findByNoAndTokenAndExpDtAfterAndUsedDtNull(token.tn, token.token, LocalDateTime.now())
            ?.let { ResultStatus("OK") }
            ?: ResultStatus("FAIL")

    @Transactional
    fun recoverPassword(arp: AccountRecoverPasswordRequest): ResultStatus {
        val auth = accountRecoverAuthRepository.findByNoAndTokenAndExpDtAfterAndUsedDtNull(arp.tn, arp.token, LocalDateTime.now())
                ?: return ResultStatus("FAIL", "이메일 인증이 만료되었습니다.")

        val account = auth.account
                ?: return ResultStatus("FAIL", "해당 메일인증에서 계정정보를 찾을 수 없습니다.")

        accountRecoverAuthRepository.save(auth.apply { usedDt = LocalDateTime.now() })
        accountRepository.save(account.apply { password = passwordEncoder.encode(arp.password) })

        return ResultStatus("OK")
    }


    fun findEmailByName(name: String): ResultData<Map<String, String>?> =
        accountRepository.findWithRolesByName(name)
            ?.takeIf { it.lastLoginDt.isBefore(LocalDateTime.parse("2021-02-09T00:00:00")) }
            ?.let {
                var email = it.email.let { e ->
                    val arr = e.split("@".toRegex()).toMutableList()
                    arr[0] = masking(arr[0])
                    arr.joinToString("@")
                }
                ResultData("OK", "", mapOf("account" to masking(it.oldAccount), "email" to email))
            }
            ?: ResultData("FAIL", "2021년 02월 09일 이후 로그인에 성공했거나 존재하지 않는 닉네임입니다.")

    private fun masking(text: String) =
        if (text.length > 3) text.substring(0, 2) + "***" +  text.substring(text.length - 1) else "$text***"

}
