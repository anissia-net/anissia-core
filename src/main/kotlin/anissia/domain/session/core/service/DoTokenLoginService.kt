package anissia.domain.session.core.service

import anissia.domain.account.core.ports.outbound.AccountRepository
import anissia.domain.session.core.LoginFail
import anissia.domain.session.core.model.DoTokenLoginCommand
import anissia.domain.session.core.model.GetLoginInfoItemCommand
import anissia.domain.session.core.model.LoginInfoItem
import anissia.domain.session.core.model.Session
import anissia.domain.session.core.ports.inbound.DoTokenLogin
import anissia.domain.session.core.ports.inbound.GetLoginInfoItem
import anissia.domain.session.core.ports.outbound.LoginFailRepository
import anissia.domain.session.core.ports.outbound.LoginTokenRepository
import anissia.shared.ResultWrapper
import gs.shared.FailException
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class DoTokenLoginService(
    private val accountRepository: AccountRepository,
    private val loginFailRepository: LoginFailRepository,
    private val loginTokenRepository: LoginTokenRepository,
    private val getLoginInfoItem: GetLoginInfoItem
): DoTokenLogin {
    override fun handle(cmd: DoTokenLoginCommand, session: Session): ResultWrapper<LoginInfoItem> {
        val ip = session.ip

        if (loginFailRepository.countByIpAndEmailAndFailDtAfter(ip, "#${cmd.tn}", OffsetDateTime.now().plusMinutes(-30)) >= 10) {
            throw FailException("잦은 접속시도로 일정시간동안 차단되었습니다.\n시간이 지난 후 다시 시도해주세요.")
        }

        loginTokenRepository.findByTokenNoAndTokenAndExpDtAfter(cmd.tn, cmd.token, OffsetDateTime.now())
            ?.let { token ->
                accountRepository.findWithRolesByAn(token.an)
                    ?.also { account ->

                        accountRepository.save(account.apply { lastLoginDt = OffsetDateTime.now() })

                        return getLoginInfoItem.handle(GetLoginInfoItemCommand(session = Session.cast(account, ip), makeLoginToken = true))
                    }

            }

        loginFailRepository.save(LoginFail.create(ip = ip, email = "#${cmd.tn}"))

        throw FailException("기타예외")
    }
}
