package anissia.domain.anime.application


import anissia.domain.anime.core.ports.inbound.UpdateAnimeDocument
import anissia.domain.anime.core.ports.outbound.AnimeRepository
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/anime/temp")
class AnimeTempController(
    private val animeRepository: AnimeRepository,
    private val updateAnimeDocument: UpdateAnimeDocument
) {
//    @GetMapping("/update-all-anime-document")
//    fun updateAllAnimeDocument(): String {
//        animeRepository.findAll().forEach {
//            updateAnimeDocument.handle(it)
//        }
//        return "OK!!"
//    }
}
