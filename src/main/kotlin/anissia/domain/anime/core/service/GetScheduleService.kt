package anissia.domain.anime.core.service

import anissia.domain.anime.core.model.AnimeScheduleItem
import anissia.domain.anime.core.model.GetScheduleCommand
import anissia.domain.anime.core.ports.inbound.GetSchedule
import anissia.domain.anime.core.ports.outbound.AnimeRepository
import anissia.infrastructure.common.As
import anissia.infrastructure.service.GoogleAnalyticsProxyService
import me.saro.kit.service.CacheStore
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange

@Service
class GetScheduleService(
    private val animeRepository: AnimeRepository,
    private val googleAnalyticsProxyService: GoogleAnalyticsProxyService,
): GetSchedule {

    private val scheduleCacheStore = CacheStore<String, List<AnimeScheduleItem>>((5L * 60000L))
    override fun handle(cmd: GetScheduleCommand, exchange: ServerWebExchange): List<AnimeScheduleItem> {
        cmd.validate()
        return if (cmd.useCache) {
            scheduleCacheStore.find(cmd.week) { getScheduleNotCache(cmd.week) }
                .also { googleAnalyticsProxyService.send("/api/anime/schedule/${cmd.week}", exchange) }
        } else {
            // 캐시 없는 호출은 관리자만 가능
            As.toSession(exchange).validateAdmin()
            getScheduleNotCache(cmd.week)
        }
    }

    private fun getScheduleNotCache(week: String): List<AnimeScheduleItem> =
        animeRepository
            .findAllSchedule(week)
            .map { AnimeScheduleItem(it) }
            .run {
                when(week) {
                    "7" -> sortedByDescending { if (it.time != "") it.time else "9999" }
                    "8" -> sortedBy { if (it.time != "") it.time else "9999" }
                    else -> sortedBy { it.time }
                }
            }

}
