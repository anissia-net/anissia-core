package anissia.domain.account.core.service

import anissia.domain.account.core.AccountRegisterAuth
import anissia.domain.account.core.model.RegisterCommand
import anissia.domain.account.core.ports.inbound.Register
import anissia.domain.account.core.ports.outbound.AccountBanNameRepository
import anissia.domain.account.core.ports.outbound.AccountRegisterAuthRepository
import anissia.domain.account.core.ports.outbound.AccountRepository
import anissia.domain.session.core.model.Session
import anissia.infrastructure.common.As
import anissia.infrastructure.service.AsyncService
import anissia.infrastructure.service.BCryptService
import anissia.infrastructure.service.EmailService
import anissia.shared.ResultWrapper
import me.saro.kit.Texts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Service
class RegisterService(
    private val accountBanNameRepository: AccountBanNameRepository,
    private val accountRegisterAuthRepository: AccountRegisterAuthRepository,
    private val accountRepository: AccountRepository,
    private val bCryptService: BCryptService,
    private val asyncService: AsyncService,
    @Value("\${host}") private val host: String,

    private val emailService: EmailService,
): Register {

    private val registerAuthHtml = As.getResource("/email/account-register-auth.html").readText()
    private val emailDateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")

    companion object {
        private const val EXP_HOUR = 1
    }

    @Transactional
    override fun handle(cmd: RegisterCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()

        if (session.isLogin) {
            return ResultWrapper.fail("로그인 중에는 계정을 생성할 수 없습니다.")
        }

        if (accountRepository.existsByEmail(cmd.email)) {
            return ResultWrapper.fail("이미 가입된 계정입니다.")
        }

        if (accountRepository.existsByName(cmd.name) || accountBanNameRepository.existsById(cmd.name)) {
            return ResultWrapper.fail("사용중이거나 사용할 수 없는 이름입니다.")
        }

        if (accountRegisterAuthRepository.existsByEmailAndExpDtAfter(cmd.email, OffsetDateTime.now())) {
            return ResultWrapper.fail("인증을 시도한 계정은 ${EXP_HOUR}시간동안 인증을 할 수 없습니다.")
        }

        val ip = session.ip
        val auth = accountRegisterAuthRepository.save(
            AccountRegisterAuth(
                token = Texts.createRandomBase62String(128, 256),
                email = cmd.email,
                ip = ip,
                data = As.toJsonString(cmd.apply { password = bCryptService.encode(password) }),
                expDt = OffsetDateTime.now().plusHours(1)
            )
        )

        asyncService.async {
            emailService.send(
                cmd.email,
                "[애니시아] 회원가입 이메일 인증",
                registerAuthHtml
                    .replace("[[ip]]", ip)
                    .replace("[[exp_dt]]", auth.expDt.format(emailDateFormat))
                    .replace("[[url]]", "${host}/register/${auth.no}-${auth.token}")
            )
        }

        return ResultWrapper.ok()
    }

}
