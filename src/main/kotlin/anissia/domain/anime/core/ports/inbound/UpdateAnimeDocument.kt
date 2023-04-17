package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.Anime
import anissia.domain.anime.core.AnimeDocument
import anissia.domain.anime.core.model.UpdateAnimeDocumentCommand

interface UpdateAnimeDocument{
    fun handle(cmd: UpdateAnimeDocumentCommand): AnimeDocument?

    fun handle(anime: Anime, isDelete: Boolean = false): AnimeDocument?
}
