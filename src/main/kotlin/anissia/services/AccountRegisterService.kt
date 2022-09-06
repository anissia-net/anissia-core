package anissia.services

import anissia.configruration.logger
import anissia.dto.ResultStatus
import anissia.dto.request.AccountRegisterRequest
import anissia.dto.request.EmailAuthTokenRequest
import anissia.misc.As
import anissia.rdb.entity.Account
import anissia.rdb.entity.AccountRegisterAuth
import anissia.rdb.repository.AccountBanNameRepository
import anissia.rdb.repository.AccountRegisterAuthRepository
import anissia.rdb.repository.AccountRepository
import com.fasterxml.jackson.core.type.TypeReference
import me.saro.kit.Texts
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest

@Service
class AccountRegisterService(
    private val accountRepository: AccountRepository,
    private val accountRegisterAuthRepository: AccountRegisterAuthRepository,
    private val accountBanNameRepository: AccountBanNameRepository,
    private val passwordEncoder: PasswordEncoder,
    private val asyncService: AsyncService,
    private val emailService: EmailService,
    private val request: HttpServletRequest,
    @Value("\${host}") private val host: String
) {

    private val log = logger<AccountRegisterService>()
    private val registerAuthHtml = As.getResource("/email/account-register-auth.html").readText()
    private val emailDateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")

    companion object {
        private const val EXP_HOUR = 1
    }

    @Transactional
    @Synchronized
    fun register(accountRegisterRequest: AccountRegisterRequest): ResultStatus {
        if (accountRepository.existsByEmail(accountRegisterRequest.email)) {
            return ResultStatus("FAIL", "이미 가입된 계정입니다.")
        }

        if (accountRepository.existsByName(accountRegisterRequest.name) || accountBanNameRepository.existsById(accountRegisterRequest.name)) {
            return ResultStatus("FAIL", "사용중이거나 사용할 수 없는 이름입니다.")
        }

        if (accountRegisterAuthRepository.existsByEmailAndExpDtAfter(accountRegisterRequest.email, LocalDateTime.now())) {
            return ResultStatus("FAIL", "인증을 시도한 계정은 ${EXP_HOUR}시간동안 인증을 할 수 없습니다.")
        }

        val ip = request.remoteAddr
        val auth = accountRegisterAuthRepository.save(
            AccountRegisterAuth(
                token = Texts.createRandomBase62String(128, 256),
                email = accountRegisterRequest.email,
                ip = ip,
                data = As.toJsonString(accountRegisterRequest.apply { password = passwordEncoder.encode(password) }),
                expDt = LocalDateTime.now().plusHours(1)
            )
        )

        asyncService.async {
            emailService.send(
                accountRegisterRequest.email,
                "[애니시아] 회원가입 이메일 인증",
                registerAuthHtml
                    .replace("[[ip]]", ip)
                    .replace("[[exp_dt]]", auth.expDt.format(emailDateFormat))
                    .replace("[[url]]", "${host}/register/${auth.no}-${auth.token}")
            )
        }

        return ResultStatus("OK")
    }

    @Transactional
    fun registerValidation(tokenRequest: EmailAuthTokenRequest): ResultStatus {
        val auth: AccountRegisterAuth = accountRegisterAuthRepository.findByNoAndTokenAndExpDtAfterAndUsedDtNull(tokenRequest.tn, tokenRequest.token, LocalDateTime.now())
                ?: return ResultStatus("FAIL", "이메일 인증이 만료되었습니다.")

        val register = As.OBJECT_MAPPER.readValue(auth.data, object: TypeReference<AccountRegisterRequest>() {})

        if (accountRepository.existsByEmail(register.email)) {
            return ResultStatus("FAIL", "이미 가입된 계정입니다.")
        }

        if (accountRepository.existsByName(register.name)) {
            return ResultStatus("FAIL", "사용중인 닉네임 입니다.")
        }

        accountRegisterAuthRepository.save(auth.apply { usedDt = LocalDateTime.now() })

        accountRepository.save(
            Account(
                email = register.email,
                password = register.password,
                name = register.name
            )
        )

        return ResultStatus("OK")
    }
}
