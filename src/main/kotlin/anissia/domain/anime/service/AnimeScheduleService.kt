package anissia.domain.anime.service

import anissia.domain.anime.model.AnimeScheduleItem
import anissia.domain.anime.model.GetScheduleCommand
import anissia.domain.anime.model.GetScheduleSvgCommand
import org.springframework.web.server.ServerWebExchange

interface AnimeScheduleService {
    fun get(cmd: GetScheduleCommand, exchange: ServerWebExchange): List<AnimeScheduleItem>
    fun get(cmd: GetScheduleSvgCommand, exchange: ServerWebExchange): String
}
