package anissia.dto.request

import anissia.misc.As
import anissia.rdb.entity.AnimeStatus
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Pattern


data class AnimeRequest (
    var status: String = "",
    @Pattern(regexp = "0|1|2|3|4|5|6|7|8", message = "잘못된 요일입니다.")
    var week: String = "",
    @Pattern(regexp = "[\\d]{2}:[\\d]{2}", message = "잘못된 시간입니다.")
    var time: String = "",
    @NotEmpty(message = "애니메이션 제목을 입력해주세요.")
    var subject: String = "",
    var genres: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var website: String = ""
) {
    val genresList get() = genres.split(",".toRegex())
    val statusEnum get() = AnimeStatus.valueOf(status)
    fun validate() {
        As.throwHttp400If("애니메이션 제목을 입력해주세요.", subject.isBlank())
        As.throwHttp400Exception("존재하지 않는 상태입니다.") { statusEnum }
        As.throwHttp400If("장르가 입력되지 않았습니다.", genres.isBlank())
        As.throwHttp400If("장르는 3개까지만 입력가능합니다.", genresList.size > 3)
        As.throwHttp400If("시작일이 규격에 맞지 않습니다.", !As.isAsAnimeDate(startDate))
        As.throwHttp400If("종료일이 규격에 맞지 않습니다.", !As.isAsAnimeDate(endDate))
        As.throwHttp400If("시작일은 종료일보다 미래일 수 없습니다.", startDate.isNotEmpty() && endDate.isNotEmpty() && startDate > endDate)
        As.throwHttp400If("사이트주소는 공백이거나 http:// https:// 로시작해야합니다.", !As.isWebSite(website, true))
    }
}
