package anissia.domain.account.service

import anissia.domain.account.Account
import anissia.domain.account.AccountRecoverAuth
import anissia.domain.account.model.RecoverCommand
import anissia.domain.account.repository.AccountRecoverAuthRepository
import anissia.domain.account.repository.AccountRepository
import anissia.domain.session.model.Session
import anissia.infrastructure.common.As
import anissia.infrastructure.service.AsyncService
import anissia.infrastructure.service.EmailService
import anissia.shared.ResultWrapper
import me.saro.kit.TextKit
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Service
class RecoverService(
    private val accountRecoverAuthRepository: AccountRecoverAuthRepository,
    private val accountRepository: AccountRepository,
    @Value("\${host}") private val host: String,
    private val asyncService: AsyncService,
    private val emailService: EmailService,
): Recover {

    private val log = As.logger<RecoverService>()
    private val recoverAuthHtml = As.getResource("/email/account-recover-auth.html").readText()
    private val emailDateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")

    companion object {
        private const val EXP_HOUR = 1L
    }

    @Transactional
    override fun handle(cmd: RecoverCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()

        var account: Account = accountRepository.findByEmailAndName(cmd.email, cmd.name)
        // not exist match info is secret for protect personal information
        // 개인정보보호를 위해 일치 하지 않는경우에도 일치하는 것과 같은 정보를 내보낸다.
            ?: return ResultWrapper.ok()

        if (accountRecoverAuthRepository.existsByAnAndExpDtAfter(account.an, OffsetDateTime.now())) {
            return ResultWrapper.fail("인증을 시도한 계정은 ${EXP_HOUR}시간동안 인증을 할 수 없습니다.")
        }

        val ip = session.ip

        val auth = accountRecoverAuthRepository
            .save(
                AccountRecoverAuth(
                token = TextKit.generateBase62(128, 256),
                an = account.an,
                ip = ip,
                expDt = OffsetDateTime.now().plusHours(EXP_HOUR))
            )

        asyncService.async {
            emailService.send(
                cmd.email,
                "[애니시아] 계정 복원 이메일 인증",
                recoverAuthHtml
                    .replace("[[ip]]", ip)
                    .replace("[[exp_dt]]", emailDateFormat.format(auth.expDt))
                    .replace("[[url]]", "${host}/recover/${auth.no}-${auth.token}")
            )
        }

        return ResultWrapper.ok()
    }

}
