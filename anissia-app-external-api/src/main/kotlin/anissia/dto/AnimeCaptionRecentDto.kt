package anissia.dto

import anissia.rdb.domain.AnimeCaption
import anissia.misc.As

data class AnimeCaptionRecentDto (
        var subject: String = "",
        var episode: String = "",
        var updDt: String = "",
        var website: String = "",
        var name: String = ""
) {
        constructor(animeCaption: AnimeCaption): this(
                subject = animeCaption.anime!!.subject,
                episode = animeCaption.episode,
                updDt = animeCaption.anime!!.updDt.format(As.DTF_ISO_CAPTION) + ":00",
                website = animeCaption.website,
                name = animeCaption.account!!.name
        )
}
