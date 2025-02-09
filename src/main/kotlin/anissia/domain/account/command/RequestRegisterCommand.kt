package anissia.domain.account.command

import anissia.infrastructure.common.REGEX_MAIL
import anissia.infrastructure.common.REGEX_NAME

class RequestRegisterCommand(
    var email: String = "",
    var password: String = "",
    var name: String = ""
) {
    fun validate() {
        if (email.isBlank()) throw IllegalArgumentException("계정(이메일)을 입력해주세요.")
        if (password.isBlank()) throw IllegalArgumentException("암호를 입력해주세요.")
        if (name.isBlank()) throw IllegalArgumentException("이름을 입력해주세요.")
        if (!email.matches(REGEX_MAIL)) throw IllegalArgumentException("이메일 형식이 아닙니다.")
        if (!name.matches(REGEX_NAME)) throw IllegalArgumentException("닉네임은 특수문자를 제외한\n한글/영어/숫자/한자/일어로\n2자이상 16자 이하로 입력해주세요.")
    }
}
