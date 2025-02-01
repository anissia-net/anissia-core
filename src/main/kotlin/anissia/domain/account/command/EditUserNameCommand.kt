package anissia.domain.account.command

import anissia.infrastructure.common.REGEX_NAME

class EditUserNameCommand (
    var password: String = "",
    var name: String = ""
) {
    fun validate() {
        if (password.isBlank()) throw IllegalArgumentException("암호를 입력해 주세요.")
        if (name.isBlank()) throw IllegalArgumentException("이름을 입력해주세요.")
        if (!name.matches(REGEX_NAME)) throw IllegalArgumentException("닉네임은 특수문자를 제외한\n한글/영어/숫자/한자/일어로\n2자이상 16자 이하로 입력해주세요.")
    }
}
