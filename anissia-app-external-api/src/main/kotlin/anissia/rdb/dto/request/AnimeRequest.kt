package anissia.rdb.dto.request

import anissia.misc.As
import anissia.rdb.domain.AnimeStatus
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern


data class AnimeRequest (
    var status: String = "",
    @Pattern(regexp = "0|1|2|3|4|5|6|7|8", message = "잘못된 요일입니다.")
    var week: String = "",
    @Pattern(regexp = "[\\d]{2}:[\\d]{2}", message = "잘못된 시간입니다.")
    var time: String = "",
    @NotBlank(message = "제목을 입력해주세요.")
    var subject: String = "",
    var genres: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var website: String = ""
) {
    fun validate() {
        As.throwHttp400IfError("존재하지 않는 상태입니다.") { AnimeStatus.valueOf(status) }
        As.throwHttp400IfError("장르가 입력되지 않았습니다.") { genres.isBlank() }
        As.throwHttp400IfError("장르는 3개까지만 입력가능합니다.") { genres.split(",".toRegex()).size > 3 }
        As.throwHttp400IfError("시작일이 규격에 맞지 않습니다.") {
            if (startDate.isNotEmpty()) LocalDateTime.parse(startDate, As.DTF_ISO_YMD)
        }
        As.throwHttp400IfError("종료일이 규격에 맞지 않습니다.") {
            if (endDate.isNotEmpty()) LocalDateTime.parse(endDate, As.DTF_ISO_YMD)
        }
        As.throwHttp400If("사이트주소는 공백이거나 http:// https:// 로시작해야합니다.") {
            !(website == "" || website.startsWith("https://") || website.startsWith("http://"))
        }
    }
}
