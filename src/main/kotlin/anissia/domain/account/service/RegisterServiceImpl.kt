package anissia.domain.account.service

import anissia.domain.account.Account
import anissia.domain.account.AccountRegisterAuth
import anissia.domain.account.command.CompleteRegisterCommand
import anissia.domain.account.command.RequestRegisterCommand
import anissia.domain.account.repository.AccountBanNameRepository
import anissia.domain.account.repository.AccountRegisterAuthRepository
import anissia.domain.account.repository.AccountRepository
import anissia.domain.session.model.SessionItem
import anissia.infrastructure.common.*
import anissia.infrastructure.service.EmailService
import anissia.shared.ApiFailException
import com.fasterxml.jackson.core.type.TypeReference
import me.saro.kit.TextKit
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Service
class RegisterServiceImpl(
    private val accountBanNameRepository: AccountBanNameRepository,
    private val accountRegisterAuthRepository: AccountRegisterAuthRepository,
    private val accountRepository: AccountRepository,
    @Value("\${host}") private val host: String,
    private val emailService: EmailService,
): RegisterService {

    private val trRequestRegisterCommand = object: TypeReference<RequestRegisterCommand>() {}
    private val registerAuthHtml = "/email/account-register-auth.html".toResource.readText()
    private val emailDateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")

    companion object {
        private const val EXP_HOUR = 1
    }

    @Transactional
    override fun request(cmd: RequestRegisterCommand, sessionItem: SessionItem): Mono<String> =
        Mono.defer {
            cmd.validate()
            if (sessionItem.isLogin) {
                Mono.error(ApiFailException("로그인 중에는 계정을 생성할 수 없습니다."))
            } else if (accountRepository.existsByEmail(cmd.email)) {
                Mono.error(ApiFailException("이미 가입된 계정입니다."))
            } else if (accountRepository.existsByName(cmd.name) || accountBanNameRepository.existsById(cmd.name)) {
                Mono.error(ApiFailException("사용중이거나 사용할 수 없는 이름입니다."))
            } else if (accountRegisterAuthRepository.existsByEmailAndExpDtAfter(cmd.email, OffsetDateTime.now())) {
                Mono.error(ApiFailException("인증을 시도한 계정은 ${EXP_HOUR}시간동안 인증을 할 수 없습니다."))
            } else {

                val ip = sessionItem.ip
                val auth = accountRegisterAuthRepository.save(
                    AccountRegisterAuth(
                        token = TextKit.generateBase62(128, 256),
                        email = cmd.email,
                        ip = ip,
                        data = cmd.apply { password = password.enBCrypt }.toJson,
                        expDt = OffsetDateTime.now().plusHours(1)
                    )
                )

                emailService.send(
                    cmd.email,
                    "[애니시아] 회원가입 이메일 인증",
                    registerAuthHtml
                        .replace("[[ip]]", ip)
                        .replace("[[exp_dt]]", auth.expDt.format(emailDateFormat))
                        .replace("[[url]]", "${host}/register/${auth.no}-${auth.token}")
                ).subscribeBoundedElastic()

                Mono.just("")
            }
        }

    @Transactional
    override fun complete(cmd: CompleteRegisterCommand): Mono<String> =
        Mono.defer {
            cmd.validate()
            accountRegisterAuthRepository.findByNoAndTokenAndExpDtAfterAndUsedDtNull(cmd.tn, cmd.token, OffsetDateTime.now())
                ?.let { Mono.just(it) }
                ?: Mono.error(ApiFailException("이메일 인증이 만료되었습니다."))
        }
            .flatMap { auth ->
                val requestRegisterCommand = auth.data.toClassByJson(trRequestRegisterCommand)

                if (accountRepository.existsByEmail(requestRegisterCommand.email)) {
                    Mono.error(ApiFailException("이미 가입된 계정입니다."))
                } else if (accountRepository.existsByName(requestRegisterCommand.name)) {
                    Mono.error(ApiFailException("사용중인 닉네임 입니다."))
                } else {
                    accountRegisterAuthRepository.save(auth.apply { usedDt = OffsetDateTime.now() })

                    accountRepository.save(
                        Account(
                            email = requestRegisterCommand.email,
                            password = requestRegisterCommand.password,
                            name = requestRegisterCommand.name
                        )
                    )

                    Mono.just("")
                }
            }
}
