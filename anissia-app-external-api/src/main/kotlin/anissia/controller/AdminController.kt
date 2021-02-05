package anissia.controller

import anissia.rdb.dto.AnimeDto
import anissia.rdb.dto.request.AdminCaptionRequest
import anissia.services.AdminService
import anissia.services.AnimeService
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val adminService: AdminService,
    private val animeService: AnimeService
) {

    @GetMapping("/anime/list/{page:[\\d]+}")
    fun getAnimeList(@RequestParam q: String?, @PathVariable page: Int): Page<AnimeDto> = animeService.getList(q ?: "", page)
    
    @GetMapping("/anime/delist/{page:[\\d]+}")
    fun getAnimeDelist(@PathVariable page: Int): Page<AnimeDto> = animeService.getDelist(page)

    @GetMapping("/anime/animeNo/{animeNo:[\\d]+}")
    fun getAnime(@PathVariable animeNo: Long): AnimeDto = animeService.getAnime(animeNo)

    @GetMapping("/caption/list/{active}/{page}")
    fun getCaptionList(@PathVariable active: Int, @PathVariable page: Int) = adminService.getCaptionList(active, page)

    @PostMapping("/caption/{animeNo}")
    fun addCaption(@PathVariable animeNo: Long) = adminService.addCaption(animeNo)

    @PutMapping("/caption/{animeNo}")
    fun updateCaption(@PathVariable animeNo: Long, @Valid adminCaptionRequest: AdminCaptionRequest) = adminService.updateCaption(animeNo, adminCaptionRequest)

    @DeleteMapping("/caption/{animeNo}")
    fun deleteCaption(@PathVariable animeNo: Long) = adminService.deleteCaption(animeNo)
}
