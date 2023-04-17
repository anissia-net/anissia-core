package anissia.domain.anime.core.model

import anissia.domain.anime.core.AnimeCaption
import anissia.infrastructure.common.As
import java.time.OffsetDateTime

data class MyCaptionItem (
    var animeNo: Long = 0,
    var subject: String = "",
    var episode: String = "0",
    var updDt: String = OffsetDateTime.now().format(As.DTF_ISO_CAPTION),
    var website: String = ""
) {
        constructor(animeCaption: AnimeCaption): this(
                subject = animeCaption.anime?.subject?:"제목없음",
                animeNo = animeCaption.anime?.animeNo?:0,
                episode = animeCaption.episode,
                updDt = animeCaption.updDt.format(As.DTF_ISO_CAPTION),
                website = animeCaption.website
        )
}
