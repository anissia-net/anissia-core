package anissia.domain.anime.model

import anissia.domain.anime.AnimeCaption
import anissia.infrastructure.common.DTF_ISO_CAPTION

class AnimeCaptionItem (
    val episode: String = "",
    val updDt: String = "",
    val website: String = "",
    val name: String = ""
) {
    constructor(animeCaption: AnimeCaption): this(
            episode = animeCaption.episode,
            updDt = animeCaption.updDt.format(DTF_ISO_CAPTION) + ":00",
            website = animeCaption.website,
            name = animeCaption.account?.name?:"탈퇴회원"
    )
}
