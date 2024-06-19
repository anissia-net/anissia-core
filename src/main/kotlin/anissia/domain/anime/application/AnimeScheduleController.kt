package anissia.domain.anime.application


import anissia.domain.anime.core.model.AnimeScheduleItem
import anissia.domain.anime.core.model.GetScheduleCommand
import anissia.domain.anime.core.model.GetScheduleSvgCommand
import anissia.domain.anime.core.ports.inbound.GetSchedule
import anissia.domain.anime.core.ports.inbound.GetScheduleSvg
import anissia.shared.ResultWrapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/anime")
class AnimeScheduleController(
    private val getSchedule: GetSchedule,
    private val getScheduleSvg: GetScheduleSvg,
) {
    @GetMapping("/schedule/{week:[0-8]}")
    fun getSchedule(cmd: GetScheduleCommand, exchange: ServerWebExchange): ResultWrapper<List<AnimeScheduleItem>> =
        ResultWrapper.ok(getSchedule.handle(cmd, exchange))

    // 포멧이 svg 이기 때문에 ResultWrapper 를 사용하지 않는다.
    @GetMapping("/schedule/svg/{width:\\d{3}}/{color:[a-f\\d]{36}}", produces = ["image/svg+xml;charset=utf-8"])
    fun getScheduleSvg(cmd: GetScheduleSvgCommand, exchange: ServerWebExchange): String =
        getScheduleSvg.handle(cmd, exchange)
}
