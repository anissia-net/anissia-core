package anissia.services

import anissia.dto.ResultData
import anissia.dto.Session
import anissia.misc.As
import anissia.repository.AccountRepository
import me.saro.kit.Texts
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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







    fun doLogin(loginRequest: LoginRequest): ResultData<Session> {
        val ip = request.remoteAddr

        if (!checkLoginFailCount(ip, loginRequest.email)) {
            return ResultData("FAIL", "잦은 접속시도로 일정시간동안 차단되었습니다.", null)
        }

        // try login
        accountRepository.findWithRolesByEmail(loginRequest.email)?.apply {
            if (isBan) {
                return ResultData("FAIL", "${banExpDt!!.format(As.DTF_YMDHMS)} 까지 차단된 계정입니다.", null)
            }

            if (passwordEncoder.matches(loginRequest.password, password)) {
                accountRepository.save(this.apply {
                    password = passwordEncoder.encode(loginRequest.password)
                    lastDt = LocalDateTime.now()
                })

                val userSessionData = Session.cast(this)
                        .apply { context.authentication = DhantAuthentication(this) }

                val token = loginRequest.takeIf { it.tokenLogin == 1 }
                        ?.let { updateLoginToken(LoginToken(an = an)).absoluteToken }?:""

                // clean up and return
                loginFailRepository.deleteByIpAndAccount(ip, loginRequest.email)
                loginPassRepository.save(LoginPass(an = an, connType = "login", ip = ip))
                return ResultData("OK", token, userSessionData)
            }
        }

        // login fail
        loginFailRepository.save(LoginFail(ip = ip, account = loginRequest.email))
        return ResultData("FAIL", "계정(email)과 암호가 일치하지 않습니다.", null)
    }

    fun doTokenLogin(loginTokenRequest: LoginTokenRequest): ResultData<Session> {
        val ip = request.remoteAddr

        if (!checkLoginFailCount(ip, loginTokenRequest.tn.toString())) {
            return ResultData("FAIL", "", null)
        }

        // try find token
        loginTokenRepository.findByTnAndTokenAndExpDtAfter(loginTokenRequest.tn, loginTokenRequest.token, LocalDateTime.now())?.apply {
            val token = this
            accountRepository.findWithRolesByAn(an)?.apply {
                accountRepository.save(this.apply { lastDt = LocalDateTime.now() })

                val userSessionData = Session.cast(this)
                        .apply { context.authentication = DhantAuthentication(this) }

                // clean up and return
                loginFailRepository.deleteByIpAndAccount(ip, loginTokenRequest.tn.toString())
                loginPassRepository.save(LoginPass(an = token.an, connType = "token", ip = ip))
                return ResultData("OK", updateLoginToken(token).absoluteToken, userSessionData)
            }
        }

        // token login fail
        loginFailRepository.save(LoginFail(ip = ip, account = loginTokenRequest.tn.toString()))
        return ResultData("FAIL", "", null)
    }

    fun doLogout() = context.run { authentication = null; ResultStatus("OK") }

    fun checkLoginFailCount(ip: String, account: String)
            = loginFailRepository.countByIpAndAccountAndFailDtAfter(ip, account, LocalDateTime.now().plusMinutes(-30)) < 5

    fun updateLoginToken(loginToken: LoginToken) = loginToken.run {
        token = Texts.createRandomBase62String(128, 512)
        expDt = LocalDateTime.now().plusDays(10)
        loginTokenRepository.save(this)
    }

    fun updateSession(account: Account?) {
        account?.takeIf { it.an > 0 }
                ?.let{ DhantAuthentication(Session.cast(it)) }
                ?.apply { context.authentication = this }
                ?:doLogout()
    }



}
