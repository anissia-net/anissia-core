package anissia.domain.session.service

import anissia.domain.account.repository.AccountRepository
import anissia.domain.session.LoginFail
import anissia.domain.session.model.DoTokenLoginCommand
import anissia.domain.session.model.GetLoginInfoItemCommand
import anissia.domain.session.model.LoginInfoItem
import anissia.domain.session.model.Session
import anissia.domain.session.repository.LoginFailRepository
import anissia.domain.session.repository.LoginTokenRepository
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
