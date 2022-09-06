package anissia.services

import anissia.dto.AnimeScheduleDto
import anissia.rdb.repository.AnimeRepository
import me.saro.kit.CacheStore
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class AnimeScheduleService(
    private val animeRepository: AnimeRepository,
    private val googleAnalyticsProxyService: GoogleAnalyticsProxyService
) {
    private val scheduleCacheStore = CacheStore<String, List<AnimeScheduleDto>>((5 * 60000).toLong())

    private val svgDateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

    fun getSchedule(week: String): List<AnimeScheduleDto> =
        scheduleCacheStore
            .find(week) { getScheduleNotCache(week) }
            .also { googleAnalyticsProxyService.send("/api/anime/schedule/$week") }

    // using for admin
    fun getScheduleNotCache(week: String): List<AnimeScheduleDto> =
        animeRepository
            .findAllSchedule(week)
            .map { AnimeScheduleDto(it) }
            .run {
                when(week) {
                    "7" -> sortedByDescending { if (it.time != "") it.time else "9999" }
                    "8" -> sortedBy { if (it.time != "") it.time else "9999" }
                    else -> sortedBy { it.time }
                }
            }

    fun getScheduleSvg(width: String, color: String): String =
        LocalDateTime.now().let { dt -> getSchedule((dt.dayOfWeek.value % 7).toString()).run {
            val titleBg = color.substring(0, 6); val title = color.substring(6, 12)
            val ymdBg = color.substring(12, 18); val ymd = color.substring(18, 24)
            val listBg = color.substring(24, 30); val list = color.substring(30, 36)
            val height = (this.size * 20 + 50).toString()
"""<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px"
     width="${width}px" height="${height}px" viewBox="0 0 $width $height" enable-background="new 0 0 $width $height" xml:space="preserve">
<rect fill="#${listBg}" width="100%" height="100%"/>
<rect fill="#${titleBg}" width="100%" height="30" />
<rect fill="#${ymdBg}" width="100%" height="20" y="30" />
<text x="0" y="0" fill="#${list}" font-family="'Malgun Gothic'" font-size="13">
<tspan x="50%" dy="20" fill="#${title}" text-anchor="middle" font-size="13" font-weight="bold">애니편성표</tspan>
<tspan x="50%" dy="25" fill="#${ymd}" text-anchor="middle" font-size="12">${dt.format(svgDateFormat)}</tspan>
${joinToString("\n") { """<tspan x="2" dy="20"><![CDATA[${it.time} ${it.subject}]]></tspan>""" }}
</text>
</svg>""" }}
            .also { googleAnalyticsProxyService.send("/api/anime/schedule/svg") }
}
