package anissia.domain.anime.controller


import anissia.domain.anime.command.GetScheduleCommand
import anissia.domain.anime.command.GetScheduleSvgCommand
import anissia.domain.anime.model.AnimeScheduleItem
import anissia.domain.anime.service.AnimeScheduleService
import anissia.infrastructure.common.toApiResponse
import anissia.shared.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/anime")
class AnimeScheduleController(
    private val animeScheduleService: AnimeScheduleService,
) {
    @GetMapping("/schedule/{week:[0-8]}")
    fun getSchedule(cmd: GetScheduleCommand, exchange: ServerWebExchange): Mono<ApiResponse<List<AnimeScheduleItem>>> =
        animeScheduleService.get(cmd, exchange).toApiResponse

    // 포멧이 svg 이기 때문에 ResultWrapper 를 사용하지 않는다.
    @GetMapping("/schedule/svg/{width:\\d{3}}/{color:[a-f\\d]{36}}", produces = ["image/svg+xml;charset=utf-8"])
    fun getScheduleSvg(cmd: GetScheduleSvgCommand, exchange: ServerWebExchange): Mono<String> =
        animeScheduleService.get(cmd, exchange)
}
