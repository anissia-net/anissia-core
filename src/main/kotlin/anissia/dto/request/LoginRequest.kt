package anissia.dto.request

import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class LoginRequest (
    @field:NotNull(message = "아이디는 E-MAIL 형식입니다.")
    @field:Size(min = 4, max = 64, message = "아이디는 E-MAIL 형식입니다.")
    //@field:Email(regexp = As.IS_MAIL, message = "이메일이 형식에 맞지 않습니다.")
    var email: String = "",

    @field:NotNull(message = "암호를 입력해주세요.")
    @field:Size(min = 8, max = 128, message = "암호는 8자리 이상 128자리 이하로 작성해야합니다.")
    var password: String = "",

    /** token for auto login - 1: enable auto login, 0: not */
    @field:NotNull(message = "암호를 입력해주세요.")
    @field:Min(0) @Max(1)
    var tokenLogin: Int = 0
)
