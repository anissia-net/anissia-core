package anissia.domain.anime.repository

import anissia.domain.anime.AnimeGenre
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface AnimeGenreRepository : ReactiveCrudRepository<AnimeGenre, String> {
    fun countByGenreIn(genre: List<String>): Mono<Long>
}
