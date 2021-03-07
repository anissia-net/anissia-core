package anissia.controller

import anissia.dto.AnimeDto
import anissia.dto.AnimeScheduleDto
import anissia.dto.request.AnimeCaptionRequest
import anissia.dto.request.AnimeRequest
import anissia.services.AdminService
import anissia.services.AnimeScheduleService
import anissia.services.AnimeService
import anissia.services.TranslatorService
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val adminService: AdminService,
    private val animeService: AnimeService,
    private val translatorService: TranslatorService,
    private val animeScheduleService: AnimeScheduleService
) {
    @GetMapping("/anime/list/{page:[\\d]+}")
    fun getAnimeList(@RequestParam q: String?, @PathVariable page: Int): Page<AnimeDto> = animeService.getList(q ?: "", page)
    
    @GetMapping("/anime/delist")
    fun getAnimeDelist(): Page<Map<String, Any>> = adminService.getAnimeDelist()

    @GetMapping("/translator/apply/count")
    fun getTranslatorApplyCount() = translatorService.getApplyCount()

    @GetMapping("/anime/animeNo/{animeNo:[\\d]+}")
    fun getAnime(@PathVariable animeNo: Long): AnimeDto = animeService.getAnime(animeNo)

    @GetMapping("/schedule/{week:[0-8]}")
    fun getSchedule(@PathVariable week: String): List<AnimeScheduleDto> = animeScheduleService.getScheduleNotCache(week)

    @GetMapping("/caption/list/{active}/{page}")
    fun getCaptionList(@PathVariable active: Int, @PathVariable page: Int) = adminService.getCaptionList(active, page)

    @PostMapping("/caption/{animeNo}")
    fun addCaption(@PathVariable animeNo: Long) = adminService.addCaption(animeNo)

    @PutMapping("/caption/{animeNo}")
    fun updateCaption(@PathVariable animeNo: Long, @Valid @RequestBody animeCaptionRequest: AnimeCaptionRequest) = adminService.updateCaption(animeNo, animeCaptionRequest)

    @DeleteMapping("/caption/{animeNo}")
    fun deleteCaption(@PathVariable animeNo: Long) = adminService.deleteCaption(animeNo)

    @PostMapping("/anime")
    fun addAnime(@Valid @RequestBody animeRequest: AnimeRequest) = adminService.addAnime(animeRequest)

    @PutMapping("/anime/{animeNo}")
    fun updateAnime(@PathVariable animeNo: Long, @Valid @RequestBody animeRequest: AnimeRequest) = adminService.updateAnime(animeNo, animeRequest)

    @DeleteMapping("/anime/{animeNo}")
    fun deleteAnime(@PathVariable animeNo: Long) = adminService.deleteAnime(animeNo)

    @PostMapping("/anime/recover/{agendaNo}")
    fun recoverAnime(@PathVariable agendaNo: Long) = adminService.recoverAnime(agendaNo)
}
