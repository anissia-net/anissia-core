package anissia.dto.request

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class AccountRecoverPasswordRequest (
    @field:NotNull(message = "유효한 토큰이 아닙니다.")
    @field:Size(min = 128, max = 550, message = "유효한 토큰이 아닙니다.")
    @field:Pattern(regexp = "[\\d]{1,20}-[\\da-zA-Z]{128,512}", message = "유효한 토큰이 아닙니다.")
    var absoluteToken: String,

    @field:NotEmpty(message = "암호는 8자이상 128자이하로 입력해주세요.")
    @field:Size(min = 8, max = 128, message = "암호는 8자이상 128자이하로 입력해주세요.")
    var password: String = "",
) {
    /** token number */
    val tn get() = absoluteToken.substringBefore('-', "0").toLong()

    /** token */
    val token get() = absoluteToken.substringAfter('-', "")
}
