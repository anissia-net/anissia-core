package anissia.domain.anime.service

import anissia.domain.anime.command.GetScheduleCommand
import anissia.domain.anime.command.GetScheduleSvgCommand
import anissia.domain.anime.model.AnimeScheduleItem
import anissia.domain.anime.repository.AnimeRepository
import anissia.infrastructure.common.MonoCacheStore
import anissia.infrastructure.common.sessionItem
import anissia.infrastructure.common.subscribeBoundedElastic
import anissia.infrastructure.service.GoogleAnalyticsProxyService
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Service
class AnimeScheduleServiceImpl(
    private val animeRepository: AnimeRepository,
    private val googleAnalyticsProxyService: GoogleAnalyticsProxyService,
): AnimeScheduleService {

    private val scheduleCacheStore = MonoCacheStore<String, List<AnimeScheduleItem>>((5L * 60000L))
    private val svgDateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

    override fun get(cmd: GetScheduleCommand, exchange: ServerWebExchange): Mono<List<AnimeScheduleItem>> =
        Mono.defer {
            cmd.validate()
            if (cmd.useCache) {
                scheduleCacheStore.find(cmd.week) { getScheduleNotCache(cmd.week) }
                    .doOnNext { googleAnalyticsProxyService.send("/api/anime/schedule/${cmd.week}", exchange).subscribeBoundedElastic() }
            } else {
                // 캐시 없는 호출은 관리자만 가능
                exchange.sessionItem.validateAdmin()
                getScheduleNotCache(cmd.week)
            }
        }

    override fun get(cmd: GetScheduleSvgCommand, exchange: ServerWebExchange): Mono<String> =
        Mono.defer {
            cmd.validate()
            val now = OffsetDateTime.now()
            val color = cmd.color
            val titleBgColor = color.substring(0, 6)
            val titleColor = color.substring(6, 12)
            val ymdBgColor = color.substring(12, 18)
            val ymdColor = color.substring(18, 24)
            val listBgColor = color.substring(24, 30)
            val listColor = color.substring(30, 36)
            val width = cmd.width
            get(GetScheduleCommand("${now.dayOfWeek.value % 7}", true), exchange)
                .map { schedule ->
                    val height = schedule.size * 20 + 50
                    """<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="${width}px" height="${height}px" viewBox="0 0 $width $height" enable-background="new 0 0 $width $height" xml:space="preserve">
<rect fill="#${listBgColor}" width="100%" height="100%"/>
<rect fill="#${titleBgColor}" width="100%" height="30" />
<rect fill="#${ymdBgColor}" width="100%" height="20" y="30" />
<text x="0" y="0" fill="#${listColor}" font-family="'Malgun Gothic'" font-size="13">
<tspan x="50%" dy="20" fill="#${titleColor}" text-anchor="middle" font-size="13" font-weight="bold">애니편성표</tspan>
<tspan x="50%" dy="25" fill="#${ymdColor}" text-anchor="middle" font-size="12">${now.format(svgDateFormat)}</tspan>
${schedule.joinToString("\n") { """<tspan x="2" dy="20"><![CDATA[${it.time} ${it.subject}]]></tspan>""" }}
</text>
</svg>"""
                }
        }

    private fun getScheduleNotCache(week: String): Mono<List<AnimeScheduleItem>> =
        Flux.just(*animeRepository.findAllSchedule(week).toTypedArray())
            .map { AnimeScheduleItem(it) }
            .collectList()
            .map { schedule ->
                when(week) {
                    "7" -> schedule.sortedByDescending { if (it.time != "") it.time else "9999" }
                    "8" -> schedule.sortedBy { if (it.time != "") it.time else "9999" }
                    else -> schedule.sortedBy { it.time }
                }
            }

}
