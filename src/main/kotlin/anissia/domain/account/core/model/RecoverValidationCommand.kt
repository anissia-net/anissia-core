package anissia.domain.account.core.model

class RecoverValidationCommand(
    var absoluteToken: String
) {
    /** token number */
    val tn get() = absoluteToken.substringBefore('-', "0").toLong()

    /** token */
    val token get() = absoluteToken.substringAfter('-', "")

    fun validate() {
        if (absoluteToken.isBlank()) throw IllegalArgumentException("토큰이 없습니다.")
        if (absoluteToken.length < 128 || absoluteToken.length > 600) throw IllegalArgumentException("토큰이 잘못되었습니다.")
    }
}
