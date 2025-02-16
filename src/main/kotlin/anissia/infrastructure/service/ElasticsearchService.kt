package anissia.infrastructure.service

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.*
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

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
        .defaultHeaders {
            it.set("Content-Type", "application/json")
            it.set("Accept", "application/json")
            if ((username + password).isNotBlank()) {
                it.setBasicAuth(username, password)
            }
        }
        .build()

    fun requestRaw(method: HttpMethod, uri: String, body: String? = null): WebClient.RequestBodySpec =
        elasticClient.method(method)
            .uri(uri).apply { if (body != null) bodyValue(body) }

    fun requestStateOk(method: HttpMethod, uri: String, body: String? = null): Boolean =
        requestRaw(method, uri, body)
            .exchangeToMono { Mono.just(it.statusCode().is2xxSuccessful) }
            .subscribeOn(Schedulers.boundedElastic())
            .toFuture().get() ?: false

    fun request(method: HttpMethod, uri: String, body: String? = null): JsonNode =
        requestRaw(method, uri, body).retrieve().bodyToMono(JsonNode::class.java)
            .subscribeOn(Schedulers.boundedElastic())
            .toFuture().get()!!

    fun existsIndex(index: String): Boolean =
        requestStateOk(HEAD, "/$index")

    fun deleteIndex(index: String): Boolean =
        requestStateOk(DELETE, "/$index")

    fun deleteIndexIfExists(index: String): Boolean =
        if (existsIndex(index)) {
            deleteIndex(index)
        } else false

    fun createIndex(index: String, body: String): Boolean =
        requestStateOk(PUT, "/$index", body)

    fun updateIndex(forceCreate: Boolean, index: String, body: String): Boolean =
        requestStateOk(PUT, "/$index/_mapping", body)
}
