package anissia.dto.request

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class AccountUpdatePasswordRequest (
    @field:NotEmpty(message = "암호는 8자이상 128자이하로 입력해주세요.")
    @field:Size(min = 8, max = 128, message = "암호는 8자이상 128자이하로 입력해주세요.")
    var oldPassword: String = "",

    @field:NotEmpty(message = "암호는 8자이상 128자이하로 입력해주세요.")
    @field:Size(min = 8, max = 128, message = "암호는 8자이상 128자이하로 입력해주세요.")
    var newPassword: String = "",
)
