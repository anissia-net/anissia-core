package anissia.domain.anime.service

import reactor.core.publisher.Mono

interface AnimeGenreGenres {
    fun get(): Mono<List<String>>
}
