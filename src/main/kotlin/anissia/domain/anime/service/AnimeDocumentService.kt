package anissia.domain.anime.service

import anissia.domain.anime.Anime
import anissia.domain.anime.model.UpdateAnimeDocumentCommand
import anissia.domain.session.model.Session

interface AnimeDocumentService{
    fun update(cmd: UpdateAnimeDocumentCommand)
    fun update(anime: Anime, isDelete: Boolean = false)
    fun reset()
}
