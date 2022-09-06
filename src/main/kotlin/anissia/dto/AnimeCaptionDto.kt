package anissia.dto

import anissia.misc.As
import anissia.rdb.entity.AnimeCaption

data class AnimeCaptionDto (
        var episode: String = "",
        var updDt: String = "",
        var website: String = "",
        var name: String = ""
) {
        constructor(animeCaption: AnimeCaption): this(
                episode = animeCaption.episode,
                updDt = animeCaption.updDt.format(As.DTF_ISO_CAPTION) + ":00",
                website = animeCaption.website,
                name = animeCaption.account?.name?:"탈퇴회원"
        )
}
