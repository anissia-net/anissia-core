package anissia.domain.anime.core.model

import anissia.domain.anime.core.AnimeCaption
import anissia.infrastructure.common.As

data class AnimeCaptionItem (
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
