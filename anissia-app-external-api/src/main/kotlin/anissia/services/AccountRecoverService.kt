//package anissia.services
//
//import anissia.configruration.logger
//import anissia.misc.As
//import anissia.rdb.dto.ResultData
//import anissia.rdb.repository.AccountRecoverAuthRepository
//import anissia.rdb.repository.AccountRepository
//import me.saro.kit.Texts
//import me.saro.kit.Valids
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.stereotype.Service
//import org.springframework.transaction.annotation.Isolation
//import org.springframework.transaction.annotation.Transactional
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//
//@Service
//class AccountRecoverService(
//    val accountRepository: AccountRepository,
//    val accountRecoverAuthRepository: AccountRecoverAuthRepository,
//    val passwordEncoder: PasswordEncoder,
//    var emailService: EmailService,
//    @Value("\${host}") private val host: String
//) {
//
//    private val log = logger<AccountRecoverService>()
//    private val recoverAuthHtml = As.getResource("/email/recover-auth.html").readText()
//    private val emailDateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")
//
//    companion object {
//        private const val EXP_HOUR = 1L
//    }
//
//    fun requestMailAuth(recoverRequestEmailAuthRequest: RecoverRequestEmailAuthRequest, ip: String): ResultData<String> {
//        var user = accountRepository.findWithRolesByAccount(recoverRequestEmailAuthRequest.account)
//
//        // it is temp code
//        if (user == null) {
//            user = accountRepository.findWithRolesByMail(recoverRequestEmailAuthRequest.account)
//        }
//
//        if (user == null) {
//            return ResultData("FAIL", "해당 이메일로 가입된 계정이 없습니다.")
//        }
//
//        if (accountRecoverAuthRepository.existsByUnAndExpDtAfter(user.un, LocalDateTime.now())) {
//            return ResultData("FAIL", "인증을 시도한 계정은 ${EXP_HOUR}시간동안 인증을 할 수 없습니다.")
//        }
//
//        val expDt = LocalDateTime.now().plusHours(EXP_HOUR)
//
//        if (user.birth == "0000-00-00" || user.birth == recoverRequestEmailAuthRequest.birth) {
//            val auth = accountRecoverAuthRepository
//                    .save(UserRecoverAuth(token = Texts.createRandomBase62String(128, 256), un = user.un, ip = ip, expDt = expDt))
//
//            emailService.asyncSendWithoutResult(
//                    recoverRequestEmailAuthRequest.account,
//                    "[애니시아] 계정 복원 이메일 인증",
//                    recoverAuthHtml
//                            .replace("[[ip]]", ip)
//                            .replace("[[exp_dt]]", emailDateFormat.format(auth.expDt))
//                            .replace("[[url]]", "${host}/recover?token=${auth.no}-${auth.token}")
//            )
//        } else {
//            // dummy insert
//            accountRecoverAuthRepository.save(UserRecoverAuth(token = "FAIL", un = user.un, ip = ip, usedDt = LocalDateTime.now(), expDt = expDt))
//        }
//
//        return ResultData("OK")
//    }
//
//    fun verifyMailAuth(tokenRequest: RecoverEmailAuthTokenRequest): ResultData<String>
//        = if (accountRecoverAuthRepository.findByNoAndTokenAndExpDtAfterAndUsedDtNull(tokenRequest.tn, tokenRequest.token, LocalDateTime.now()) != null)
//            ResultData("OK")
//        else
//            ResultData("FAIL")
//
//
//    @Transactional(isolation = Isolation.SERIALIZABLE)
//    fun changePassword(rcp: RecoverChangePasswordRequest): ResultData<String> {
//        val auth: UserRecoverAuth = accountRecoverAuthRepository.findByNoAndTokenAndExpDtAfterAndUsedDtNull(rcp.tn, rcp.token, LocalDateTime.now())
//                ?: return ResultData("FAIL", "이메일 인증이 만료되었습니다.")
//
//        val user = auth.user
//                ?: return ResultData("FAIL", "해당 메일인증에서 유저정보를 찾을 수 없습니다.")
//
//        // execute change password
//        accountRecoverAuthRepository.save(auth.apply { usedDt = LocalDateTime.now() })
//        accountRepository.save(user.apply {
//            password = passwordEncoder.encode(rcp.password)
//            // temp -------------------------------------
//            if (!Valids.isMail(account, 64)) {
//                account = mail
//            }
//        })
//
//        return ResultData("OK")
//    }
//
//}
