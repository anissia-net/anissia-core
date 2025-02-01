package anissia.domain.session.service

import anissia.domain.account.repository.AccountRepository
import anissia.domain.session.LoginFail
import anissia.domain.session.command.DoTokenLoginCommand
import anissia.domain.session.command.DoUserLoginCommand
import anissia.domain.session.command.GetJwtAuthInfoCommand
import anissia.domain.session.model.JwtAuthInfoItem
import anissia.domain.session.model.SessionItem
import anissia.domain.session.repository.LoginFailRepository
import anissia.domain.session.repository.LoginTokenRepository
import anissia.infrastructure.common.eqBCrypt
import anissia.shared.ApiFailException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@Service
class LoginServiceImpl(
    private val accountRepository: AccountRepository,
    private val loginFailRepository: LoginFailRepository,
    private val loginTokenRepository: LoginTokenRepository,
    private val jwtService: JwtService,
): LoginService {
    override fun doUserLogin(cmd: DoUserLoginCommand, sessionItem: SessionItem): Mono<JwtAuthInfoItem> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .flatMap { loginFailRepository.countByIpAndEmailAndFailDtAfter(sessionItem.ip, cmd.email, OffsetDateTime.now().plusMinutes(-30)) }
            .filter { it > 10 }
            .switchIfEmpty(Mono.error(ApiFailException("잦은 접속시도로 일정시간동안 차단되었습니다.\n시간이 지난 후 다시 시도해주세요.")))
            .flatMap { accountRepository.findWithRolesByEmail(cmd.email) }
            .filter { it.password.eqBCrypt(cmd.password) }
            .switchIfEmpty(Mono.error(ApiFailException("암호가 일치하지 않거나 존재하지 않는 계정입니다.")))
            .flatMap { account -> accountRepository.save(account.apply { lastLoginDt = OffsetDateTime.now() }) }
            .flatMap { account -> jwtService.getAuthInfo(GetJwtAuthInfoCommand(sessionItem = SessionItem.cast(account, sessionItem.ip), makeLoginToken = cmd.makeLoginToken)) }


    override fun doTokenLogin(cmd: DoTokenLoginCommand, sessionItem: SessionItem): Mono<JwtAuthInfoItem> =
        Mono.just(cmd)
            .flatMap { loginFailRepository.countByIpAndEmailAndFailDtAfter(sessionItem.ip, cmd.tn.toString(), OffsetDateTime.now().plusMinutes(-30)) }
            .filter { it > 10 }
            .switchIfEmpty(Mono.error(ApiFailException("잦은 접속시도로 일정시간동안 차단되었습니다.\n시간이 지난 후 다시 시도해주세요.")))
            .flatMap { loginTokenRepository.findByTokenNoAndTokenAndExpDtAfter(cmd.tn, cmd.token, OffsetDateTime.now()) }
            .flatMap { token -> accountRepository.findWithRolesByAn(token.an) }
            .flatMap { account -> accountRepository.save(account.apply { lastLoginDt = OffsetDateTime.now() }).map { account } }
            .flatMap { account -> jwtService.getAuthInfo(GetJwtAuthInfoCommand(sessionItem = SessionItem.cast(account, sessionItem.ip), makeLoginToken = true)) }
            .switchIfEmpty(
                loginFailRepository.save(LoginFail.create(ip = sessionItem.ip, email = "#${cmd.tn}"))
                    .then(Mono.error(ApiFailException("암호가 일치하지 않거나 존재하지 않는 계정입니다.")))
            )
}
