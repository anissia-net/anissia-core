package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.GetScheduleSvgCommand
import org.springframework.web.server.ServerWebExchange

interface GetScheduleSvg {
    fun handle(cmd: GetScheduleSvgCommand, exchange: ServerWebExchange): String
}
