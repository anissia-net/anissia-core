package anissia.domain.anime.core.model

import anissia.domain.anime.core.AnimeCaption
import anissia.infrastructure.common.As

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
            updDt = animeCaption.updDt.format(As.DTF_ISO_CAPTION) + ":00",
            website = animeCaption.website,
            name = animeCaption.account!!.name
    )
}
