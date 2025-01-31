package anissia.domain.anime.repository

import anissia.domain.anime.Anime
import anissia.infrastructure.common.logger
import anissia.infrastructure.common.toJson
import anissia.infrastructure.service.ElasticsearchService
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AnimeDocumentRepository(
    private val elasticsearch: ElasticsearchService,
) {
    private val log = logger<AnimeDocumentRepository>()
    private val index = "anissia_anime"

    fun update(anime: Anime, translators: List<String>): Mono<JsonNode> =
        Mono.just(
            mapOf(
                "animeNo" to anime.animeNo,
                "week" to anime.week,
                "subject" to anime.subject + " " + anime.originalSubject,
                "status" to anime.status.name,
                "genres" to anime.genres.split(",".toRegex()),
                "translators" to translators,
                "endDate" to anime.endDate.replace("-", "").run { if (isEmpty()) 0L else toLong() }
            ).toJson
        )
            .doOnNext { body -> log.info("Updated anime document: $body") }
            .flatMap { body -> elasticsearch.request(HttpMethod.PUT, "/$index/_doc/${anime.animeNo}", body) }

    fun deleteByAnimeNo(animeNo: Long): Mono<JsonNode> =
        elasticsearch.request(HttpMethod.DELETE, "/$index/_doc/$animeNo")

    fun dropAndCreateIndex(): Mono<Int> =
        elasticsearch.deleteIndexIfExists(index)
            .doOnNext { log.info("Dropped index: $index") }
            .flatMap {
                elasticsearch.createIndex(index, """{"mappings":{"properties": {
                    "animeNo": {"type": "long","store": true},
                    "week": {"type": "keyword"},
                    "subject": {"type": "text"},
                    "genres": {"type": "keyword"},
                    "status": {"type": "keyword"},
                    "translators": {"type": "keyword"},
                    "endDate": {"type": "long"}
                }}}""".trimIndent())
            }
            .doOnNext { log.info("Created index: $index") }
            .thenReturn(1)
}
