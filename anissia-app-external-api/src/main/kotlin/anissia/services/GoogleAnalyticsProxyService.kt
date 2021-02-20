package anissia.services

import anissia.misc.As.Companion.encodeUrl
import anissia.misc.As.Companion.getHttp400
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import javax.servlet.http.HttpServletRequest


@Service
class GoogleAnalyticsProxyService(
    private val request: HttpServletRequest,
    private val asyncService: AsyncService,
    @Value("\${google.analytics.id}") private val id: String
) {
    fun send(path: String) {
        val ip = request.remoteAddr
        val ua = request.getHeader("user-agent") ?: throw getHttp400("does not exist user-agent")

        asyncService.async {
            val body = "v=1&tid=$id&cid=$ip&t=pageview&dp=${path.encodeUrl()}&uip=$ip&ua=${ua.encodeUrl()}"
            println(body)
            HttpRequest.newBuilder()
                .uri(URI.create("https://www.google-analytics.com/collect"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build()
                .run { HttpClient.newHttpClient().send(this, BodyHandlers.ofString()) }
                .body()
        }
    }
}
