package anissia.domain.account.core.model

class RecoverPasswordCommand(
    var absoluteToken: String,
    var password: String = "",
) {
    /** token number */
    val tn get() = absoluteToken.substringBefore('-', "0").toLong()

    /** token */
    val token get() = absoluteToken.substringAfter('-', "")

    fun validate() {
        if (password.isBlank()) throw IllegalArgumentException("암호를 입력해주세요.")
        if (password.length < 8 || password.length > 128) throw IllegalArgumentException("암호는 8자 이상 128자 이하로 입력해 주세요.")
        if (absoluteToken.isBlank()) throw IllegalArgumentException("토큰이 없습니다.")
        if (absoluteToken.length < 128 || absoluteToken.length > 600) throw IllegalArgumentException("토큰이 잘못되었습니다.")
    }
}
