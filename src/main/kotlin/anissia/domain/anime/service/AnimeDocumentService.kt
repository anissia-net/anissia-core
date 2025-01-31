package anissia.domain.anime.service

import anissia.domain.anime.Anime
import anissia.domain.anime.command.UpdateAnimeDocumentCommand
import reactor.core.publisher.Mono

interface AnimeDocumentService{
    fun update(cmd: UpdateAnimeDocumentCommand): Mono<Void>
    fun update(anime: Anime, isDelete: Boolean): Mono<Void>
    fun reset(drop: Boolean): Mono<Void>
}
