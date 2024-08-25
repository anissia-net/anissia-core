package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.Anime
import anissia.domain.anime.core.model.UpdateAnimeDocumentCommand

interface UpdateAnimeDocument{
    fun handle(cmd: UpdateAnimeDocumentCommand)
    fun handle(anime: Anime, isDelete: Boolean = false)
    fun reset()
}
