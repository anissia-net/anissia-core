package anissia.domain.anime.application


import anissia.domain.anime.core.model.*
import anissia.domain.anime.core.ports.inbound.*
import anissia.domain.anime.core.ports.outbound.AnimeRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/anime/temp")
class AnimeTempController(
    private val animeRepository: AnimeRepository,
    private val updateAnimeDocument: UpdateAnimeDocument
) {
//    @GetMapping("/update-all-anime-document")
//    fun updateAllAnimeDocument() {
//        animeRepository.findAll().forEach {
//            updateAnimeDocument.handle(it)
//        }
//    }
}
