package anissia.controller

import anissia.misc.As
import anissia.services.AnimeRankService
import anissia.services.MigrationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@RestController
class MigrationController(
    val migrationService: MigrationService,
    val animeRankService: AnimeRankService,
    private val request: HttpServletRequest
) {

    @GetMapping("/mig")
    fun mig(): String = migrationService.migration().run { "OK" }

    @GetMapping("/rank")
    fun rank(): String {

        for (animeNo in 1..2000) {
            if ((Math.random() * 10).toInt() != 0) {
                continue
            }
            for (hit in 0..(Math.random() * 30).toInt()) {
                animeRankService.hitAsync(animeNo.toLong(), ip(), hour())
            }
        }

        animeRankService.animeRankBatch()
        return "OK"
    }

    fun hour(): Long = LocalDateTime.now().minusHours((Math.random() * 480).toLong()).format(As.DTF_RANK_HOUR).toLong()

    fun ip(): String = "${(Math.random() * 256).toInt()}.${(Math.random() * 256).toInt()}.${(Math.random() * 256).toInt()}.${(Math.random() * 256).toInt()}"
}