package anissia.domain.account.service

import anissia.domain.account.Account
import anissia.domain.account.AccountRecoverAuth
import anissia.domain.account.command.CompleteRecoverPasswordCommand
import anissia.domain.account.command.RequestRecoverPasswordCommand
import anissia.domain.account.command.ValidateRecoverPasswordCommand
import anissia.domain.account.repository.AccountRecoverAuthRepository
import anissia.domain.account.repository.AccountRepository
import anissia.domain.session.model.SessionItem
import anissia.infrastructure.common.enBCrypt
import anissia.infrastructure.common.logger
import anissia.infrastructure.common.subscribeBoundedElastic
import anissia.infrastructure.common.toResource
import anissia.infrastructure.service.EmailService
import anissia.shared.ApiFailException
import me.saro.kit.TextKit
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Service
class RecoverPasswordServiceImpl(
    private val accountRecoverAuthRepository: AccountRecoverAuthRepository,
    private val accountRepository: AccountRepository,
    @Value("\${host}") private val host: String,
    private val emailService: EmailService,
): RecoverPasswordService {

    private val log = logger<RecoverPasswordServiceImpl>()
    private val recoverAuthHtml = "/email/account-recover-auth.html".toResource.readText()
    private val emailDateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")

    companion object {
        private const val EXP_HOUR = 1L
    }

    @Transactional
    override fun request(cmd: RequestRecoverPasswordCommand, sessionItem: SessionItem): Mono<String> =
        Mono.defer {
            cmd.validate()
            val account: Account? = accountRepository.findByEmailAndName(cmd.email, cmd.name)

            if (account != null) {
                if (accountRecoverAuthRepository.existsByAnAndExpDtAfter(account.an, OffsetDateTime.now())) {
                    return@defer Mono.error(ApiFailException("인증을 시도한 계정은 ${EXP_HOUR}시간동안 인증을 할 수 없습니다."))
                }

                val ip = sessionItem.ip
                val auth = accountRecoverAuthRepository.save(AccountRecoverAuth(
                    token = TextKit.generateBase62(128, 256),
                    an = account.an,
                    ip = ip,
                    expDt = OffsetDateTime.now().plusHours(EXP_HOUR))
                )

                emailService.send(
                    cmd.email,
                    "[애니시아] 계정 복원 이메일 인증",
                    recoverAuthHtml
                        .replace("[[ip]]", ip)
                        .replace("[[exp_dt]]", emailDateFormat.format(auth.expDt))
                        .replace("[[url]]", "${host}/recover/${auth.no}-${auth.token}")
                ).subscribeBoundedElastic()
            }

            // 개인정보보호를 위해 일치 하지 않는경우에도 일치하는 것과 같은 정보를 내보낸다.
            Mono.just("")
        }

    @Transactional
    override fun complete(cmd: CompleteRecoverPasswordCommand): Mono<String> =
        Mono.defer<AccountRecoverAuth> {
            cmd.validate()
            Mono.justOrEmpty(
                accountRecoverAuthRepository.findByNoAndTokenAndExpDtAfterAndUsedDtNull(cmd.tn, cmd.token, OffsetDateTime.now())
            )
        }
            .switchIfEmpty(Mono.error(ApiFailException("이메일 인증이 만료되었습니다.")))
            .filter { auth -> auth.account != null }
            .switchIfEmpty(Mono.error(ApiFailException("해당 메일인증에서 계정정보를 찾을 수 없습니다.")))
            .map { auth ->
                accountRecoverAuthRepository.save(auth.apply { usedDt = OffsetDateTime.now() })
                accountRepository.save(auth.account!!.apply { password = cmd.password.enBCrypt })
                ""
            }

    @Transactional
    override fun validate(cmd: ValidateRecoverPasswordCommand): Mono<String> =
        Mono.defer {
            cmd.validate()
            Mono.justOrEmpty(accountRecoverAuthRepository.findByNoAndTokenAndExpDtAfterAndUsedDtNull(cmd.tn, cmd.token, OffsetDateTime.now())?.let { "" })
        }

}
