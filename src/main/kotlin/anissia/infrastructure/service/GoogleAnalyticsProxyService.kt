package anissia.infrastructure.service

import anissia.infrastructure.common.encodeUrl
import anissia.infrastructure.common.getHttp400
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Service
class GoogleAnalyticsProxyService(
    @Value("\${google.analytics.id}") private val id: String
) {
    private val apiClient = WebClient.builder().baseUrl("https://www.google-analytics.com/collect").build()

    fun send(path: String, exchange: ServerWebExchange): Mono<Any> =
        Mono.just(exchange.request)
            .flatMap { request ->
                val ip = request.remoteAddress?.address?.hostAddress!!
                val ua = request.headers["user-agent"]
                    ?.firstOrNull()
                    ?.encodeUrl
                    ?: return@flatMap Mono.error<Unit>(getHttp400("does not exist user-agent"))

                apiClient.post()
                    .bodyValue("v=1&tid=$id&cid=$ip&t=pageview&dp=${path.encodeUrl}&uip=$ip&ua=$ua")
                    .exchangeToMono { Mono.just(it.statusCode().is2xxSuccessful) }
            }
}
