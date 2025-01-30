package anissia.domain.account.service

import anissia.domain.account.Account
import anissia.domain.account.AccountRegisterAuth
import anissia.domain.account.command.CompleteRegisterCommand
import anissia.domain.account.command.RequestRegisterCommand
import anissia.domain.account.repository.AccountBanNameRepository
import anissia.domain.account.repository.AccountRegisterAuthRepository
import anissia.domain.account.repository.AccountRepository
import anissia.domain.session.model.SessionItem
import anissia.infrastructure.common.enBCrypt
import anissia.infrastructure.common.toClassByJson
import anissia.infrastructure.common.toJson
import anissia.infrastructure.common.toResource
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

    private val registerAuthHtml = "/email/account-register-auth.html".toResource.readText()
    private val emailDateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")
    private val trRequestRegisterCommand: TypeReference<RequestRegisterCommand> = object: TypeReference<RequestRegisterCommand>() {}

    companion object {
        private const val EXP_HOUR = 1
    }

    @Transactional
    override fun request(cmd: RequestRegisterCommand, sessionItem: SessionItem): Mono<Void> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .filter { !sessionItem.isLogin }
            .switchIfEmpty(Mono.error(ApiFailException("로그인 중에는 계정을 생성할 수 없습니다.")))
            .filterWhen { accountRepository.existsByEmail(cmd.email).map { !it } }
            .switchIfEmpty(Mono.error(ApiFailException("이미 가입된 계정입니다.")))
            .filterWhen { accountRepository.existsByName(cmd.name).map { !it } }
            .filterWhen { accountBanNameRepository.existsById(cmd.name).map { !it } }
            .switchIfEmpty(Mono.error(ApiFailException("사용중이거나 사용할 수 없는 이름입니다.")))
            .filterWhen { accountRegisterAuthRepository.existsByEmailAndExpDtAfter(cmd.email, OffsetDateTime.now()).map { !it } }
            .switchIfEmpty(Mono.error(ApiFailException("인증을 시도한 계정은 ${EXP_HOUR}시간동안 인증을 할 수 없습니다.")))
            .flatMap {
                accountRegisterAuthRepository.save(
                    AccountRegisterAuth(
                        token = TextKit.generateBase62(128, 256),
                        email = cmd.email,
                        ip = sessionItem.ip,
                        data = cmd.apply { password = cmd.password.enBCrypt }.toJson,
                        expDt = OffsetDateTime.now().plusHours(1)
                    )
                )
            }
            .doOnNext { auth ->
                emailService.send(
                    cmd.email,
                    "[애니시아] 회원가입 이메일 인증",
                    registerAuthHtml
                        .replace("[[ip]]", auth.ip)
                        .replace("[[exp_dt]]", auth.expDt.format(emailDateFormat))
                        .replace("[[url]]", "${host}/register/${auth.no}-${auth.token}")
                )
            }
            .then()


    @Transactional
    override fun complete(cmd: CompleteRegisterCommand): Mono<Void> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .flatMap { accountRegisterAuthRepository.findByNoAndTokenAndExpDtAfterAndUsedDtNull(cmd.tn, cmd.token, OffsetDateTime.now()) }
            .switchIfEmpty(Mono.error(ApiFailException("이메일 인증이 만료되었습니다.")))
            .flatMap { accountRegisterAuth ->
                val requestRegisterCommand = accountRegisterAuth.data.toClassByJson(trRequestRegisterCommand)
                accountRepository.existsByEmail(requestRegisterCommand.email).filter { !it }
                    .switchIfEmpty(Mono.error(ApiFailException("이미 가입된 계정입니다.")))
                    .flatMap { accountRepository.existsByName(requestRegisterCommand.name).filter { !it } }
                    .switchIfEmpty(Mono.error(ApiFailException("사용중인 닉네임 입니다.")))
                    .flatMap { accountRegisterAuthRepository.save(accountRegisterAuth.apply { usedDt = OffsetDateTime.now() }) }
                    .flatMap { accountRepository.save(
                        Account(
                            email = requestRegisterCommand.email,
                            password = requestRegisterCommand.password,
                            name = requestRegisterCommand.name
                        )
                    ) }
                    .then()
            }
}
