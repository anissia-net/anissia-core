package anissia.domain.anime.service

import anissia.domain.anime.Anime
import anissia.domain.anime.command.UpdateAnimeDocumentCommand
import reactor.core.publisher.Mono

interface AnimeDocumentService{
    fun update(cmd: UpdateAnimeDocumentCommand): Mono<String>
    fun update(anime: Anime, isDelete: Boolean): Mono<String>
    fun reset(drop: Boolean): Mono<String>
}
