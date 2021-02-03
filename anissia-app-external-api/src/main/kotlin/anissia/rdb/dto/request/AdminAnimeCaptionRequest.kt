package anissia.rdb.dto.request

import anissia.misc.As
import org.springframework.core.MethodParameter
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.MethodArgumentNotValidException
import java.time.LocalDateTime
import javax.validation.constraints.Pattern

data class AdminAnimeCaptionRequest (
        @field:Pattern(regexp = "\\d{1,5}|\\d{1,5}\\.\\d{1,2}", message = "화수는 숫자여야합니다.\n최대 (5자리.소수점2자리)")
        var episode: String = "0",
        @field:DateTimeFormat(pattern = As.DTF_CAPTION_S)
        var updDt: LocalDateTime = LocalDateTime.now(),
        var website: String = ""


) {
        fun validate() {
                if (!(website == "" || website.startsWith("https://") || website.startsWith("http://"))) {
                        As.throwHttp400(this, "website", "사이트주소는 공백이거나 http:// https:// 로시작해야합니다.")
                }
        }
}
