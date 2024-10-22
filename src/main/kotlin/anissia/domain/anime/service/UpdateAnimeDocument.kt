package anissia.domain.anime.service

import anissia.domain.anime.Anime
import anissia.domain.anime.model.UpdateAnimeDocumentCommand

interface UpdateAnimeDocument{
    fun handle(cmd: UpdateAnimeDocumentCommand)
    fun handle(anime: Anime, isDelete: Boolean = false)
    fun reset()
}
