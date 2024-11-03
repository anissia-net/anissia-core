package anissia.domain.anime.service

import anissia.domain.anime.repository.AnimeGenreRepository
import me.saro.kit.service.CacheStore
import org.springframework.stereotype.Service

@Service
class AnimeGenreGenresImpl(
    private val animeGenreRepository: AnimeGenreRepository,
): AnimeGenreGenres {
    private val genresCacheStore = CacheStore<String, List<String>>(60 * 60000)
    override fun get(): List<String> =
        genresCacheStore.find("genre") {
            animeGenreRepository.findAll()
                .map { it.genre }
                .apply { sorted() }
        }
}
