package anissia.domain.anime.model

import anissia.domain.anime.AnimeCaption
import anissia.infrastructure.common.DTF_ISO_CAPTION

class CaptionRecentItem (
    val animeNo: Long = 0,
    val subject: String = "",
    val episode: String = "",
    val updDt: String = "",
    val website: String = "",
    val name: String = ""
) {
    constructor(animeCaption: AnimeCaption): this(
            animeNo = animeCaption.anime?.animeNo?:0,
            subject = animeCaption.anime!!.subject,
            episode = animeCaption.episode,
            updDt = animeCaption.updDt.format(DTF_ISO_CAPTION) + ":00",
            website = animeCaption.website,
            name = animeCaption.account!!.name
    )
}
