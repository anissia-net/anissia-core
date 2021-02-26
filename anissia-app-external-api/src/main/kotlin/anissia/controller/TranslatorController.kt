package anissia.controller

import anissia.dto.AnimeDto
import anissia.rdb.domain.TranslatorService
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/translator")
class TranslatorController(
    private val translatorService: TranslatorService
) {
    @GetMapping("/anime/list/{page:[\\d]+}")
    fun getAnimeList(@PathVariable page: Int) = translatorService.getApplyList(page)
}