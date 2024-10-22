package anissia.domain.anime.service

import anissia.domain.anime.model.AnimeScheduleItem
import anissia.domain.anime.model.GetScheduleCommand
import org.springframework.web.server.ServerWebExchange

interface GetSchedule {
    fun handle(cmd: GetScheduleCommand, exchange: ServerWebExchange): List<AnimeScheduleItem>
}
