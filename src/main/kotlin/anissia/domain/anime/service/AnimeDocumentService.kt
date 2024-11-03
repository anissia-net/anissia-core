package anissia.domain.anime.service

import anissia.domain.anime.Anime
import anissia.domain.anime.model.UpdateAnimeDocumentCommand

interface AnimeDocumentService{
    fun update(cmd: UpdateAnimeDocumentCommand)
    fun update(anime: Anime, isDelete: Boolean = false)
    fun reset()
}
