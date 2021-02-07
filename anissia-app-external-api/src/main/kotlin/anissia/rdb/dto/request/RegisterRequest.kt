package anissia.rdb.dto.request

import anissia.misc.As
import me.saro.kit.Valids
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class RegisterRequest (
    @field:NotNull(message = "")
    @field:Size(min = 1, max = 64, message = "이메일이 형식에 맞지 않습니다.")
    @field:Email(regexp = Valids.IS_MAIL, message = "이메일이 형식에 맞지 않습니다.")
    var email: String = "",

    @field:NotNull(message = "암호는 8자이상 128자이하로 입력해주세요.")
    @field:Size(min = 8, max = 128, message = "암호는 8자이상 128자이하로 입력해주세요.")
    var password: String = "",

    @field:NotNull(message = "")
    @field:Pattern(regexp=As.IS_NAME, message = "닉네임에 미허용 특수문자가 있습니다.")
    var name: String = ""
)
