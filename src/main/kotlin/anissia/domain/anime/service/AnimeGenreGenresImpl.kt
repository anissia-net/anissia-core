package anissia.domain.anime.service

import anissia.domain.anime.repository.AnimeGenreRepository
import anissia.infrastructure.common.MonoCacheStore
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AnimeGenreGenresImpl(
    private val animeGenreRepository: AnimeGenreRepository,
): AnimeGenreGenres {
    private val genresCacheStore = MonoCacheStore<String, List<String>>(60 * 60000)

    override fun get(): Mono<List<String>> =
        genresCacheStore.find("genre") {
            Mono.just(animeGenreRepository.findAll().map { it.genre }.apply { sorted() })
        }
}
