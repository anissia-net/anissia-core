package anissia.domain.anime.application


import anissia.domain.anime.core.ports.inbound.UpdateAnimeDocument
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/anime/temp")
class AnimeTempController(
    private val updateAnimeDocument: UpdateAnimeDocument
) {
    @GetMapping("/update-all-anime-document")
    fun updateAllAnimeDocument(): String {
        //updateAnimeDocument.reset()
        return "OK!!"
    }
}
