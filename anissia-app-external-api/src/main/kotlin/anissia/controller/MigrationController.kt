package anissia.controller

import anissia.services.AnimeRankService
import anissia.services.MigrationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MigrationController(
    val migrationService: MigrationService,
    val animeRankService: AnimeRankService
) {

    @GetMapping("/mig")
    fun mig(): String = migrationService.migration().run { "OK" }

    @GetMapping("/rank")
    fun rank(): String {
        animeRankService.mergeAnimeHit()
        animeRankService.extractRank()
        return "OK"
    }
}