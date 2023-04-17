package anissia.infrastructure.service

import anissia.infrastructure.common.As.Companion.encodeUrl
import anissia.infrastructure.common.As.Companion.getHttp400
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ServerWebExchange


@Service
class GoogleAnalyticsProxyService(
    private val asyncService: AsyncService,
    private val restTemplate: RestTemplate,
    @Value("\${google.analytics.id}") private val id: String
) {
    fun send(path: String, exchange: ServerWebExchange) {
        val ip = exchange.request.remoteAddress?.address?.hostAddress!!
        val ua = exchange.request.headers["user-agent"]?.firstOrNull() ?: throw getHttp400("does not exist user-agent")

        asyncService.async {
            val request = "v=1&tid=$id&cid=$ip&t=pageview&dp=${path.encodeUrl()}&uip=$ip&ua=${ua.encodeUrl()}"
            restTemplate.postForObject("https://www.google-analytics.com/collect", request, ByteArray::class.java)
        }
    }
}
