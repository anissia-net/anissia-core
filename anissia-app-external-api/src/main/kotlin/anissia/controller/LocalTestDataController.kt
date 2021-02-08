package anissia.controller

import anissia.AnissiaCoreApplication
import anissia.configruration.logger
import anissia.misc.As
import anissia.services.AnimeRankService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime


@RestController
class LocalTestDataController(
    private val animeRankService: AnimeRankService,
    @Value("\${env}") private val env: String
) {
    var log = logger<AnissiaCoreApplication>()

    // make a anime rank test data
    @GetMapping("/rank")
    fun rank(): String {
        validEnv()

        for (animeNo in 1..2000) {
            if ((Math.random() * 10).toInt() != 0) {
                continue
            }
            for (hit in 0..(Math.random() * 30).toInt()) {
                animeRankService.hitAsync(animeNo.toLong(), fakeRandomIp, fakeRandomHour)
            }
        }

        animeRankService.animeRankBatch()
        return "OK"
    }

    private val fakeRandomHour get() = LocalDateTime.now().minusHours((Math.random() * 480).toLong()).format(As.DTF_RANK_HOUR).toLong()
    private val fakeRandomIp get() = "${(Math.random() * 256).toInt()}.${(Math.random() * 256).toInt()}.${(Math.random() * 256).toInt()}.${(Math.random() * 256).toInt()}"

    private fun validEnv() {
        if (env == "prod") {
            throw AccessDeniedException("개발 서버에서만 사용할 수 있는 기능입니다.")
        }
    }
}