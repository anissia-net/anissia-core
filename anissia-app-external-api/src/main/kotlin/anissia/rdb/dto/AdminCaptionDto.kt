package anissia.rdb.dto

import anissia.rdb.domain.AnimeCaption
import anissia.misc.As
import java.time.LocalDateTime

data class AdminCaptionDto (
    var animeNo: Long = 0,
    var subject: String = "",
    var episode: String = "0",
    var updDt: String = LocalDateTime.now().format(As.DTF_ISO_CAPTION),
    var website: String = ""
) {
        constructor(animeCaption: AnimeCaption): this(
                subject = animeCaption.anime?.subject?:"제목없음",
                animeNo = animeCaption.animeNo,
                episode = animeCaption.episode,
                updDt = animeCaption.updDt.format(As.DTF_ISO_CAPTION),
                website = animeCaption.website
        )
}
