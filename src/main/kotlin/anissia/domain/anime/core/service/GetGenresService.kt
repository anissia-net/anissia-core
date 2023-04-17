package anissia.domain.anime.core.service

import anissia.domain.anime.core.ports.inbound.GetGenres
import anissia.domain.anime.core.ports.outbound.AnimeGenreRepository
import me.saro.kit.CacheStore
import org.springframework.stereotype.Service

@Service
class GetGenresService(
    private val animeGenreRepository: AnimeGenreRepository,
): GetGenres {
    private val genresCacheStore = CacheStore<String, List<String>>(60 * 60000)
    override fun handle(): List<String> =
        genresCacheStore.find("genre") {
            animeGenreRepository.findAll()
                .map { it.genre }
                .apply { sorted() }
        }
}
