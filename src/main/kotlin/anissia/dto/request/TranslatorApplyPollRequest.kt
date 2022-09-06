package anissia.dto.request

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class TranslatorApplyPollRequest (

        @field:Pattern(regexp = "-1|0|1|", message = "유효한 값이 아닙니다.")
        var point: String = "",
        @field:NotEmpty(message = "의견을 입력해주세요.")
        @field:Size(min = 1, max = 64, message = "의견을 입력해주세요.")
        var comment: String = ""
)
