package anissia.domain.anime.service

import anissia.domain.anime.model.GetScheduleSvgCommand
import org.springframework.web.server.ServerWebExchange

interface GetScheduleSvg {
    fun handle(cmd: GetScheduleSvgCommand, exchange: ServerWebExchange): String
}
