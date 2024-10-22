package anissia.domain.account.model

import anissia.infrastructure.common.As

class RecoverCommand(
    var email: String = "",
    var name: String = ""
) {
    fun validate() {
        if (name.isBlank()) throw IllegalArgumentException("이름을 입력해주세요.")
        if (email.isBlank()) throw IllegalArgumentException("계정(이메일)을 입력해주세요.")
        if (!email.matches(As.IS_MAIL.toRegex())) throw IllegalArgumentException("이메일 형식이 아닙니다.")
        if (!name.matches(As.IS_NAME.toRegex())) throw IllegalArgumentException("닉네임은 특수문자를 제외한\n한글/영어/숫자/한자/일어로\n2자이상 16자 이하로 입력해주세요.")
    }
}
