package anissia.controller

import anissia.services.AnimeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/anime")
class AnimeController(
    private val animeService: AnimeService
) {




    @GetMapping("/caption/{week:\\d}")
    fun caption() {

    }

    @GetMapping("/schedule/{week:[0-8]}")
    fun schedule(@PathVariable week: String) = animeService.getSchedule(week)
}