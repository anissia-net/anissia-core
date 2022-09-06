package anissia.services

import anissia.misc.As.Companion.encodeUrl
import anissia.misc.As.Companion.getHttp400
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import javax.servlet.http.HttpServletRequest


@Service
class GoogleAnalyticsProxyService(
    private val request: HttpServletRequest,
    private val asyncService: AsyncService,
    private val restTemplate: RestTemplate,
    @Value("\${google.analytics.id}") private val id: String
) {
    fun send(path: String) {
        val ip = request.remoteAddr
        val ua = request.getHeader("user-agent") ?: throw getHttp400("does not exist user-agent")

        asyncService.async {
            val request = "v=1&tid=$id&cid=$ip&t=pageview&dp=${path.encodeUrl()}&uip=$ip&ua=${ua.encodeUrl()}"
            restTemplate.postForObject("https://www.google-analytics.com/collect", request, ByteArray::class.java)

//            val body = "v=1&tid=$id&cid=$ip&t=pageview&dp=${path.encodeUrl()}&uip=$ip&ua=${ua.encodeUrl()}"
//            HttpRequest.newBuilder()
//                .uri(URI.create("https://www.google-analytics.com/collect"))
//                .POST(HttpRequest.BodyPublishers.ofString(body))
//                .build()
//                .run { HttpClient.newHttpClient().send(this, HttpResponse.BodyHandlers.ofString()) }
//                .body()
//
//            (URL("https://www.google-analytics.com/collect").openConnection() as HttpsURLConnection)
//                .apply {
//                    requestMethod = "POST"
//                    doOutput = true
//                    connect()
//                    outputStream.use {
//                        it.write("v=1&tid=$id&cid=$ip&t=pageview&dp=${path.encodeUrl()}&uip=$ip&ua=${ua.encodeUrl()}".toByteArray())
//                        it.flush()
//                    }
//                    responseCode
//                    disconnect()
//                }

        }
    }
}
