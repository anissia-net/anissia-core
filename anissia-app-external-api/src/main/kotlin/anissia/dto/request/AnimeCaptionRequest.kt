package anissia.dto.request

import anissia.misc.As
import java.time.LocalDateTime
import javax.validation.constraints.Pattern

data class AnimeCaptionRequest (
        @field:Pattern(regexp = "\\d{1,5}|\\d{1,5}\\.\\d{1,2}", message = "화수는 숫자여야합니다.\n최대 (5자리.소수점2자리)")
        var episode: String = "0",
        var updDt: String = LocalDateTime.now().format(As.DTF_ISO_CAPTION),
        var website: String = ""
) {
        val updLdt get() = LocalDateTime.parse(updDt, As.DTF_ISO_CAPTION)

        fun validate() {
                As.throwHttp400If("사이트주소는 공백이거나 http:// https:// 로시작해야합니다.", !As.isWebSite(website, true))
                As.throwHttp400Exception("날짜가 규격에 맞지않습니다.") { updLdt }
        }
}
