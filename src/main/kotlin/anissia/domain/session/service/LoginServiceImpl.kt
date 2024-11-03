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
import anissia.infrastructure.common.As
import anissia.infrastructure.service.BCryptService
import anissia.shared.ResultWrapper
import gs.shared.FailException
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class LoginServiceImpl(
    private val bCryptService: BCryptService,
    private val accountRepository: AccountRepository,
    private val loginFailRepository: LoginFailRepository,
    private val loginTokenRepository: LoginTokenRepository,
    private val jwtService: JwtService,
): LoginService {
    private val log = As.logger<LoginServiceImpl>()

    override fun doUserLogin(cmd: DoUserLoginCommand, sessionItem: SessionItem): ResultWrapper<JwtAuthInfoItem> {
        cmd.validate()
        val ip = sessionItem.ip

        if (loginFailRepository.countByIpAndEmailAndFailDtAfter(ip, cmd.email, OffsetDateTime.now().plusMinutes(-30)) >= 10) {
            log.info("잦은 로그인 시도 : " + cmd.email)
            throw FailException("잦은 접속시도로 일정시간동안 차단되었습니다.\n시간이 지난 후 다시 시도해주세요.")
        }

        val account = accountRepository.findWithRolesByEmail(cmd.email)
            ?.takeIf { bCryptService.matches(it.password, cmd.password) }

        if (account == null) {
            loginFailRepository.save(LoginFail.create(ip = ip, email = cmd.email))
            throw FailException("암호가 일치하지 않거나 존재하지 않는 계정입니다.")
        }

        accountRepository.save(account.apply { lastLoginDt = OffsetDateTime.now() })

        return jwtService.getAuthInfo(GetJwtAuthInfoCommand(sessionItem = SessionItem.cast(account, ip), makeLoginToken = cmd.makeLoginToken))
    }

    override fun doTokenLogin(cmd: DoTokenLoginCommand, sessionItem: SessionItem): ResultWrapper<JwtAuthInfoItem> {
        val ip = sessionItem.ip

        if (loginFailRepository.countByIpAndEmailAndFailDtAfter(ip, "#${cmd.tn}", OffsetDateTime.now().plusMinutes(-30)) >= 10) {
            throw FailException("잦은 접속시도로 일정시간동안 차단되었습니다.\n시간이 지난 후 다시 시도해주세요.")
        }

        loginTokenRepository.findByTokenNoAndTokenAndExpDtAfter(cmd.tn, cmd.token, OffsetDateTime.now())
            ?.let { token ->
                accountRepository.findWithRolesByAn(token.an)
                    ?.also { account ->

                        accountRepository.save(account.apply { lastLoginDt = OffsetDateTime.now() })

                        return jwtService.getAuthInfo(GetJwtAuthInfoCommand(sessionItem = SessionItem.cast(account, ip), makeLoginToken = true))
                    }

            }

        loginFailRepository.save(LoginFail.create(ip = ip, email = "#${cmd.tn}"))

        throw FailException("기타예외")
    }
}