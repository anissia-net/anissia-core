package anissia.domain.session.service

import anissia.domain.account.Account
import anissia.domain.account.repository.AccountRepository
import anissia.domain.session.LoginFail
import anissia.domain.session.LoginToken
import anissia.domain.session.command.DoTokenLoginCommand
import anissia.domain.session.command.DoUserLoginCommand
import anissia.domain.session.command.GetJwtAuthInfoCommand
import anissia.domain.session.model.JwtAuthInfoItem
import anissia.domain.session.model.SessionItem
import anissia.domain.session.repository.LoginFailRepository
import anissia.domain.session.repository.LoginTokenRepository
import anissia.infrastructure.common.eqBCrypt
import anissia.infrastructure.common.logger
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
    private val log = logger<LoginServiceImpl>()

    override fun doUserLogin(cmd: DoUserLoginCommand, sessionItem: SessionItem): Mono<JwtAuthInfoItem> =
        Mono.defer {
            cmd.validate()
            val ip = sessionItem.ip

            if (loginFailRepository.countByIpAndEmailAndFailDtAfter(ip, cmd.email, OffsetDateTime.now().plusMinutes(-30)) >= 10) {
                log.info("잦은 로그인 시도 : " + cmd.email)
                return@defer Mono.error(ApiFailException("잦은 접속시도로 일정시간동안 차단되었습니다.\n시간이 지난 후 다시 시도해주세요."))
            }

            val account = accountRepository.findWithRolesByEmail(cmd.email)
                ?.takeIf { it.password.eqBCrypt(cmd.password) }

            if (account == null) {
                loginFailRepository.save(LoginFail.create(ip = ip, email = cmd.email))
                return@defer Mono.error(ApiFailException("암호가 일치하지 않거나 존재하지 않는 계정입니다."))
            }

            accountRepository.save(account.apply { lastLoginDt = OffsetDateTime.now() })

            jwtService.getAuthInfo(GetJwtAuthInfoCommand(sessionItem = SessionItem.cast(account, ip), makeLoginToken = cmd.makeLoginToken))
        }



    override fun doTokenLogin(cmd: DoTokenLoginCommand, sessionItem: SessionItem): Mono<JwtAuthInfoItem> =
        Mono.defer {
            val ip = sessionItem.ip

            if (loginFailRepository.countByIpAndEmailAndFailDtAfter(ip, "#${cmd.tn}", OffsetDateTime.now().plusMinutes(-30)) >= 10) {
                throw ApiFailException("잦은 접속시도로 일정시간동안 차단되었습니다.\n시간이 지난 후 다시 시도해주세요.")
            }

            Mono.justOrEmpty<LoginToken>(loginTokenRepository.findByTokenNoAndTokenAndExpDtAfter(cmd.tn, cmd.token, OffsetDateTime.now()))
                .flatMap<Account> { token -> Mono.justOrEmpty(accountRepository.findWithRolesByAn(token.an)) }
                .flatMap { account ->
                    accountRepository.save(account.apply { this.lastLoginDt = OffsetDateTime.now() })
                    jwtService.getAuthInfo(GetJwtAuthInfoCommand(sessionItem = SessionItem.cast(account, ip), makeLoginToken = true))
                }.switchIfEmpty(Mono.defer {
                    loginFailRepository.save(LoginFail.create(ip = ip, email = "#${cmd.tn}"))
                    Mono.error(ApiFailException("토큰 로그인이 실패했습니다."))
                })
        }
}
