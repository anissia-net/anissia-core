package anissia.infrastructure.service

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.*
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class ElasticsearchService(
    @Value("\${anissia.ndb.url}")
    private val url: String,
    @Value("\${anissia.ndb.username}")
    private val username: String,
    @Value("\${anissia.ndb.password}")
    private val password: String,
) {
    private val elasticClient = WebClient.builder()
        .baseUrl(url)
        .defaultHeaders { if ((username + password).isNotBlank()) { it.setBasicAuth(username, password) } }
        .build()

    fun requestRaw(method: HttpMethod, uri: String, body: String? = null): WebClient.RequestBodySpec =
        elasticClient.method(method)
            .uri(uri).apply { if (body != null) bodyValue(body) }

    fun requestStateOk(method: HttpMethod, uri: String, body: String? = null): Mono<Boolean> =
        requestRaw(method, uri, body)
            .exchangeToMono { Mono.just(it.statusCode().is2xxSuccessful) }

    fun request(method: HttpMethod, uri: String, body: String? = null): Mono<JsonNode> =
        requestRaw(method, uri, body).retrieve().bodyToMono(JsonNode::class.java)

    fun existsIndex(index: String): Mono<Boolean> =
        requestStateOk(HEAD, "/$index")

    fun deleteIndex(index: String): Mono<Boolean> =
        requestStateOk(DELETE, "/$index")

    fun deleteIndexIfExists(index: String): Mono<Boolean> =
        existsIndex(index).flatMap { if (it) deleteIndex(index) else Mono.just(false) }

    fun createIndex(index: String, body: String): Mono<Boolean> =
        requestStateOk(PUT, "/$index", body)

    fun updateIndex(forceCreate: Boolean, index: String, body: String): Mono<Boolean> =
        requestStateOk(PUT, "/$index/_mapping", body)
}
