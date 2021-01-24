package anissia.controller

import anissia.rdb.dto.AnimeCaptionDto
import anissia.rdb.dto.AnimeDto
import anissia.rdb.dto.AnimeScheduleDto
import anissia.services.AnimeRankService
import anissia.services.AnimeScheduleService
import anissia.services.AnimeService
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/anime")
class AnimeController(
    private val animeService: AnimeService,
    private val animeScheduleService: AnimeScheduleService,
    private val animeRankService: AnimeRankService
) {

    // - anime

    @GetMapping("/list/{page:[\\d]+}")
    fun getList(@RequestParam q: String?, @PathVariable page: Int): Page<AnimeDto> = animeService.getList(q ?: "", page)

    @GetMapping("/animeNo/{animeNo:[\\d]+}")
    fun getAnime(@PathVariable animeNo: Long): AnimeDto = animeService.getAnime(animeNo)

    @GetMapping("/autocorrect")
    fun getAnimeAutocorrect(@RequestParam q: String)= animeService.getAnimeAutocorrect(q)

    // - caption

    @GetMapping("/caption/animeNo/{animeNo:[\\d]+}")
    fun getCaptionByAnimeNo(@PathVariable animeNo: Long): List<AnimeCaptionDto> = animeService.getCaptionByAnimeNo(animeNo)

    // - schedule

    @GetMapping("/schedule/{week:[0-8]}")
    fun getSchedule(@PathVariable week: String): List<AnimeScheduleDto> = animeScheduleService.getSchedule(week)

    @GetMapping("/schedule/svg/{width:[\\d]{3}}/{color:[a-z\\d]{36}}", produces = ["image/svg+xml;charset=utf-8"])
    fun getScheduleSvg(@PathVariable width: String, @PathVariable color: String) = animeScheduleService.getScheduleSvg(width, color)

    // - rank

    @GetMapping("/rank/{type}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getRank(@PathVariable type: String): String = animeRankService.getRank(type)
}