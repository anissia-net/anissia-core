package anissia.domain.session.command

import anissia.infrastructure.common.As
import gs.shared.FailException

class DoLoginCommand(
    val email: String = "",
    val password: String = "",
    val makeLoginToken: Boolean = false
) {
    fun validate() {
        if (email.length < 4 || email.length > 64 || !As.IS_MAIL_REGEX.matches(email))
            throw FailException("아이디는 E-MAIL 형식입니다.")

        if (password.length < 8 || password.length > 128)
            throw FailException("암호는 8자리 이상 128자리 이하로 작성해야합니다.")
    }
}
