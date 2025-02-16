package anissia.domain.anime.repository

import anissia.domain.anime.Anime
import anissia.infrastructure.common.As
import anissia.infrastructure.service.ElasticsearchService
import com.fasterxml.jackson.core.io.JsonStringEncoder
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component

@Component
class AnimeDocumentRepository(
    private val elasticsearch: ElasticsearchService,
) {
    private val log = As.logger<AnimeDocumentRepository>()

    private val index = "anissia_anime"
    private val mapper = As.OBJECT_MAPPER
    private val jsonStringEncoder = JsonStringEncoder.getInstance()

    fun update(anime: Anime, translators: List<String>) {
        val body: String = mapper.writeValueAsString(mapOf(
            "animeNo" to anime.animeNo,
            "week" to anime.week,
            "subject" to anime.subject + " " + anime.originalSubject,
            "status" to anime.status.name,
            "genres" to anime.genres.split(",".toRegex()),
            "translators" to translators,
            "endDate" to anime.endDate.replace("-", "").run { if (isEmpty()) 0L else toLong() }
        ))
        val res = elasticsearch.requestStateOk(HttpMethod.PUT, "/$index/_doc/${anime.animeNo}", body)
        log.info("Updated anime document: $body / $res")
    }

    fun deleteByAnimeNo(animeNo: Long) =
        elasticsearch.requestStateOk(HttpMethod.DELETE, "/$index/_doc/$animeNo")

    fun dropAndCreateIndex() {
        elasticsearch.deleteIndexIfExists(index)
        log.info("Dropped index: $index")
        elasticsearch.createIndex(index, """{"mappings":{"properties": {
            "animeNo": {"type": "long","store": true},
            "week": {"type": "keyword"},
            "subject": {"type": "text"},
            "genres": {"type": "keyword"},
            "status": {"type": "keyword"},
            "translators": {"type": "keyword"},
            "endDate": {"type": "long"}
        }}}""".trimIndent())
        log.info("Created index: $index")
    }

    private val String.escape: CharArray get() =
        jsonStringEncoder.quoteAsString(this)
}
