package anissia.domain.anime.core.model

import anissia.domain.anime.core.AnimeCaption
import anissia.infrastructure.common.As
import java.time.OffsetDateTime

data class MyCaptionItem (
    val animeNo: Long = 0,
    val subject: String = "",
    val episode: String = "0",
    val updDt: String = OffsetDateTime.now().format(As.DTF_ISO_CAPTION),
    val website: String = ""
) {
    constructor(animeCaption: AnimeCaption): this(
        subject = animeCaption.anime?.subject?:"제목없음",
        animeNo = animeCaption.anime?.animeNo?:0,
        episode = animeCaption.episode,
        updDt = animeCaption.updDt.format(As.DTF_ISO_CAPTION),
        website = animeCaption.website
    )
}
