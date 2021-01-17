package anissia.dto

import anissia.domain.AnimeCaption
import anissia.misc.As

data class AdminAnimeCaptionDto (
        var animeNo: Long = 0,
        var subject: String = "",
        var episode: String = "",
        var updDt: String = "",
        var website: String = ""
) {
        constructor(animeCaption: AnimeCaption): this(
                subject = animeCaption.anime?.subject?:"제목없음",
                animeNo = animeCaption.animeNo,
                episode = animeCaption.episode,
                updDt = animeCaption.updDt.format(As.DTF_YMDHMS),
                website = animeCaption.website
        )
}
