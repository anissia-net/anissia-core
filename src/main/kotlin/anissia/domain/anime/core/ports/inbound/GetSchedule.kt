package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.AnimeScheduleItem
import anissia.domain.anime.core.model.GetScheduleCommand
import org.springframework.web.server.ServerWebExchange

interface GetSchedule {
    fun handle(cmd: GetScheduleCommand, exchange: ServerWebExchange): List<AnimeScheduleItem>
}
