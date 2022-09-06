package anissia.services

import anissia.configruration.AnissiaAuthentication
import anissia.dto.ResultData
import anissia.dto.ResultStatus
import anissia.dto.Session
import anissia.dto.request.LoginRequest
import anissia.dto.request.LoginTokenRequest
import anissia.misc.As
import anissia.rdb.entity.Account
import anissia.rdb.entity.LoginFail
import anissia.rdb.entity.LoginPass
import anissia.rdb.entity.LoginToken
import anissia.rdb.repository.AccountRepository
import anissia.rdb.repository.LoginFailRepository
import anissia.rdb.repository.LoginPassRepository
import anissia.rdb.repository.LoginTokenRepository
import me.saro.kit.Texts
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

/**
 * session service
 */
@Service
class SessionService (
    private val passwordEncoder: PasswordEncoder,
    private val accountRepository: AccountRepository,
    private val loginFailRepository: LoginFailRepository,
    private val loginPassRepository: LoginPassRepository,
    private val loginTokenRepository: LoginTokenRepository,
    private val request: HttpServletRequest
) {

    val session: Session? get() = context.authentication?.principal?.takeIf { it is Session }?.let { it as Session }
    val context: SecurityContext get() = SecurityContextHolder.getContext()
    val isManager: Boolean get() = session?.isManager() ?: false

    @Transactional
    fun doLogin(loginRequest: LoginRequest): ResultData<Session> {
        val ip = request.remoteAddr

        if (!checkLoginFailCount(ip, loginRequest.email)) {
            return ResultData("FAIL", "잦은 접속시도로 일정시간동안 차단되었습니다.\n30분뒤에 다시 시도해주세요.", null)
        }

        // try login
        accountRepository.findWithRolesByEmail(loginRequest.email)
            ?.also { account ->
                if (account.isBan) {
                    return ResultData("FAIL", "${account.banExpireDt!!.format(As.DTF_USER_YMDHMS)} 까지 차단된 계정입니다.", null)
                }

                if (passwordEncoder.matches(loginRequest.password, account.password)) {
                    accountRepository.save(account.apply { password = passwordEncoder.encode(loginRequest.password) })

                    val session = Session.cast(account)
                            .apply { context.authentication = AnissiaAuthentication(this) }

                    val token = loginRequest.takeIf { it.tokenLogin == 1 }
                            ?.let { updateLoginToken(LoginToken(an = account.an)).absoluteToken }?:""

                    // clean up and return
                    loginFailRepository.deleteByIpAndEmail(ip, loginRequest.email)
                    loginPassRepository.save(LoginPass(an = account.an, connType = "login", ip = ip))
                    return ResultData("OK", token, session)
                }
            }
            ?: accountRepository.findByOldAccount(loginRequest.email)
                ?.takeIf { account -> passwordEncoder.matches(loginRequest.password, account.password) }
                ?.let { return ResultData("FAIL", "이메일 (${it.email})로 로그인해주세요.", null) }

        // login fail
        loginFailRepository.save(LoginFail(ip = ip, email = loginRequest.email))
        return ResultData("FAIL", "계정(email)과 암호가 일치하지 않습니다.", null)
    }

    @Transactional
    fun doTokenLogin(loginTokenRequest: LoginTokenRequest): ResultData<Session> {
        val ip = request.remoteAddr

        if (!checkLoginFailCount(ip, loginTokenRequest.tokenNo.toString())) {
            return ResultData("FAIL", "", null)
        }

        loginTokenRepository.findByTokenNoAndTokenAndExpDtAfter(loginTokenRequest.tokenNo, loginTokenRequest.token, LocalDateTime.now())
            ?.let { token ->
                accountRepository.findWithRolesByAn(token.an)
                    ?.also { account ->
                        accountRepository.save(account.apply { lastLoginDt = LocalDateTime.now() })
                        val session = Session.cast(account).apply { context.authentication = AnissiaAuthentication(this) }

                        // clean up and return
                        loginFailRepository.deleteByIpAndEmail(ip, loginTokenRequest.tokenNo.toString())
                        loginPassRepository.save(LoginPass(an = token.an, connType = "token", ip = ip))
                        return ResultData("OK", updateLoginToken(token).absoluteToken, session)
                    }

            }

        // token login fail
        loginFailRepository.save(LoginFail(ip = ip, email = loginTokenRequest.tokenNo.toString()))
        return ResultData("FAIL", "", null)
    }

    fun doLogout() = context.run { authentication = null; ResultStatus("OK") }

    fun checkLoginFailCount(ip: String, account: String)
            = loginFailRepository.countByIpAndEmailAndFailDtAfter(ip, account, LocalDateTime.now().plusMinutes(-30)) < 10

    fun updateLoginToken(loginToken: LoginToken) = loginToken.run {
        token = Texts.createRandomBase62String(128, 512)
        expDt = LocalDateTime.now().plusDays(10)
        loginTokenRepository.save(this)
    }

    fun updateSession(account: Account?) {
        account?.takeIf { it.an > 0 }
                ?.let{ AnissiaAuthentication(Session.cast(it)) }
                ?.apply { context.authentication = this }
                ?:doLogout()
    }
}
