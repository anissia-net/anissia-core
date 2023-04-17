package anissia.domain.anime.core.model

import anissia.infrastructure.common.As
import java.time.LocalDateTime

class EditCaptionCommand(
    var animeNo: Long,
    val episode: String = "0",
    val updDt: String = LocalDateTime.now().format(As.DTF_ISO_CAPTION),
    val website: String = ""
) {
    val updLdt get() = LocalDateTime.parse(updDt, As.DTF_ISO_CAPTION)
    fun validate() {
        require(animeNo > 0) { "animeNo 는 0 이상이어야 합니다." }
        require(As.isWebSite(website, true)) { "사이트주소는 공백이거나 http:// https:// 로시작해야합니다." }
        try {
            updLdt
        } catch (e: Exception) {
            throw IllegalArgumentException("날짜형식이 잘못되었습니다.")
        }
        if (!Regex("\\d{1,5}|\\d{1,5}\\.\\d{1,2}").matches(episode)) {
            throw IllegalArgumentException("화수는 숫자여야합니다.\n최대 (5자리.소수점2자리)")
        }
    }
}
