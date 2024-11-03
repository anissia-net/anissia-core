package anissia.domain.account.service

import anissia.domain.account.Account
import anissia.domain.account.AccountRegisterAuth
import anissia.domain.account.command.CompleteRegisterCommand
import anissia.domain.account.command.RequestRegisterCommand
import anissia.domain.account.repository.AccountBanNameRepository
import anissia.domain.account.repository.AccountRegisterAuthRepository
import anissia.domain.account.repository.AccountRepository
import anissia.domain.session.model.SessionItem
import anissia.infrastructure.common.As
import anissia.infrastructure.service.AsyncService
import anissia.infrastructure.service.BCryptService
import anissia.infrastructure.service.EmailService
import anissia.shared.ResultWrapper
import com.fasterxml.jackson.core.type.TypeReference
import me.saro.kit.TextKit
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Service
class RegisterServiceImpl(
    private val accountBanNameRepository: AccountBanNameRepository,
    private val accountRegisterAuthRepository: AccountRegisterAuthRepository,
    private val accountRepository: AccountRepository,
    private val bCryptService: BCryptService,
    private val asyncService: AsyncService,
    @Value("\${host}") private val host: String,

    private val emailService: EmailService,
): RegisterService {

    private val registerAuthHtml = As.getResource("/email/account-register-auth.html").readText()
    private val emailDateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")

    companion object {
        private const val EXP_HOUR = 1
    }

    @Transactional
    override fun request(cmd: RequestRegisterCommand, sessionItem: SessionItem): ResultWrapper<Unit> {
        cmd.validate()

        if (sessionItem.isLogin) {
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

        val ip = sessionItem.ip
        val auth = accountRegisterAuthRepository.save(
            AccountRegisterAuth(
                token = TextKit.generateBase62(128, 256),
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

    @Transactional
    override fun complete(cmd: CompleteRegisterCommand): ResultWrapper<Unit> {
        cmd.validate()

        val auth: AccountRegisterAuth = accountRegisterAuthRepository.findByNoAndTokenAndExpDtAfterAndUsedDtNull(cmd.tn, cmd.token, OffsetDateTime.now())
            ?: return ResultWrapper.fail("이메일 인증이 만료되었습니다.")

        val requestRegisterCommand = As.OBJECT_MAPPER.readValue(auth.data, object: TypeReference<RequestRegisterCommand>() {})

        if (accountRepository.existsByEmail(requestRegisterCommand.email)) {
            return ResultWrapper.fail("이미 가입된 계정입니다.")
        }

        if (accountRepository.existsByName(requestRegisterCommand.name)) {
            return ResultWrapper.fail("사용중인 닉네임 입니다.")
        }

        accountRegisterAuthRepository.save(auth.apply { usedDt = OffsetDateTime.now() })

        accountRepository.save(
            Account(
                email = requestRegisterCommand.email,
                password = requestRegisterCommand.password,
                name = requestRegisterCommand.name
            )
        )

        return ResultWrapper.ok()
    }

}
