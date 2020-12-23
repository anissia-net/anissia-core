package anissia.controller

import anissia.dto.AnimeCaptionDto
import anissia.dto.AnimeScheduleDto
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




    @GetMapping("/caption/animeNo/{animeNo:[\\d]+}")
    fun getCaptionByAnimeNo(@PathVariable animeNo: Long): List<AnimeCaptionDto> = animeService.getCaptionByAnimeNo(animeNo)

    @GetMapping("/schedule/{week:[0-8]}")
    fun getSchedule(@PathVariable week: String): List<AnimeScheduleDto> = animeService.getSchedule(week)
}