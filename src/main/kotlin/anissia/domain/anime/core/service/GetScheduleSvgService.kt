package anissia.domain.anime.core.service

import anissia.domain.anime.core.model.AnimeScheduleItem
import anissia.domain.anime.core.model.GetScheduleCommand
import anissia.domain.anime.core.model.GetScheduleSvgCommand
import anissia.domain.anime.core.ports.inbound.GetSchedule
import anissia.domain.anime.core.ports.inbound.GetScheduleSvg
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Service
class GetScheduleSvgService(
    private val getSchedule: GetSchedule
): GetScheduleSvg {

    private val svgDateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
    override fun handle(cmd: GetScheduleSvgCommand, exchange: ServerWebExchange): String {
        cmd.validate()
        val color = cmd.color
        val width = cmd.width
        val now = OffsetDateTime.now()
        return getSchedules(now.dayOfWeek.value, exchange).run {
            val titleBg = color.substring(0, 6); val title = color.substring(6, 12)
            val ymdBg = color.substring(12, 18); val ymd = color.substring(18, 24)
            val listBg = color.substring(24, 30); val list = color.substring(30, 36)
            val height = (this.size * 20 + 50).toString()
            """<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="${width}px" height="${height}px" viewBox="0 0 $width $height" enable-background="new 0 0 $width $height" xml:space="preserve">
<rect fill="#${listBg}" width="100%" height="100%"/>
<rect fill="#${titleBg}" width="100%" height="30" />
<rect fill="#${ymdBg}" width="100%" height="20" y="30" />
<text x="0" y="0" fill="#${list}" font-family="'Malgun Gothic'" font-size="13">
<tspan x="50%" dy="20" fill="#${title}" text-anchor="middle" font-size="13" font-weight="bold">애니편성표</tspan>
<tspan x="50%" dy="25" fill="#${ymd}" text-anchor="middle" font-size="12">${now.format(svgDateFormat)}</tspan>
${joinToString("\n") { """<tspan x="2" dy="20"><![CDATA[${it.time} ${it.subject}]]></tspan>""" }}
</text>
</svg>"""
        }
    }

    private fun getSchedules(dayOfWeek: Int, exchange: ServerWebExchange): List<AnimeScheduleItem> =
        getSchedule.handle(GetScheduleCommand("${dayOfWeek % 7}", true), exchange)
}
