package anissia.domain.anime.repository

import anissia.domain.anime.Anime
import anissia.infrastructure.common.As
import anissia.infrastructure.service.ElasticsearchService
import org.elasticsearch.client.Response
import org.springframework.stereotype.Component
import java.util.*

@Component
class AnimeDocumentRepository(
    private val elasticsearch: ElasticsearchService,
) {
    private val log = As.logger<AnimeDocumentRepository>()

    private val index = "anissia_anime"
    private val mapper = As.OBJECT_MAPPER

    fun search(query: String, page: Int): Response {
        val keywords = ArrayList<String>()
        val genres = ArrayList<String>()
        val translators = ArrayList<String>()
        val end = query.indexOf("/완결") != -1

        query.lowercase(Locale.getDefault())
            .split("\\s+".toRegex())
            .stream()
            .map { it.trim() }
            .filter { it.isNotEmpty() && it != "/완결" }
            .forEach { word ->
                if (word[0] == '#' && word.length > 1) genres.add(word.substring(1))
                else if (word[0] == '@' && word.length > 1) translators.add(word.substring(1))
                else keywords.add(word)
            }

        val req = As.toJsonString(mapper.createObjectNode().apply {
            put("_source", false)
            putObject("query").apply {
                putObject("bool").apply {
                    put("minimum_should_match", "100%")

                    if (keywords.isNotEmpty() || genres.isNotEmpty()) {
                        putArray("must").apply {
                            keywords.forEach {
                                addObject().apply { putObject("wildcard").apply { put("subject", "*$it*") } }
                            }
                            genres.forEach {
                                addObject().apply { putObject("match").apply { put("genres", it) } }
                            }
                        }
                    }

                    if (translators.isNotEmpty() || end) {
                        putArray("filter").apply {

                            if (translators.isNotEmpty()) {
                                addObject().apply {
                                    putObject("bool").apply {
                                        putArray("should").apply {
                                            translators.forEach {
                                                addObject().apply {
                                                    putObject("match").apply {
                                                        put(
                                                            "translators",
                                                            it
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        put("minimum_should_match", "1")
                                    }
                                }
                            }

                            if (end) {
                                addObject().apply { putObject("match").apply { put("status", "END") } }
                            }
                        }
                    }
                }
            }
            // 종료된 애니만 검색
            if (end) {
                putArray("sort").apply { addObject().apply { putObject("endDate").apply { put("order", "desc") } } }
            }
            put("from", page * 30)
            put("size", 30)
        })

        log.info("anime search $query: $keywords $genres $translators $end")

        return elasticsearch.request("POST", "/$index/_search", req)
    }

    fun update(anime: Anime, translators: List<String>) {
        val body: String = mapper.writeValueAsString(
            mapOf(
            "animeNo" to anime.animeNo,
            "week" to anime.week,
            "subject" to anime.subject + " " + anime.originalSubject,
            "status" to anime.status.name,
            "genres" to anime.genres.split(",".toRegex()),
            "translators" to translators,
            "endDate" to anime.endDate.replace("-", "").run { if (isEmpty()) 0L else toLong() }
        ))
        val res = elasticsearch.request("PUT", "/$index/_doc/${anime.animeNo}", body)
        log.info("Updated anime document: $body / $res")
    }

    fun deleteByAnimeNo(animeNo: Long) =
        elasticsearch.request("DELETE", "/$index/_doc/$animeNo")

    fun dropAndCreateIndex() {
        elasticsearch.deleteIndexIfExists(index)
        log.info("Dropped index: $index")
        elasticsearch.createIndex(
            index, """{"mappings":{"properties": {
            "animeNo": {"type": "long","store": true},
            "week": {"type": "keyword"},
            "subject": {"type": "text"},
            "genres": {"type": "keyword"},
            "status": {"type": "keyword"},
            "translators": {"type": "keyword"},
            "endDate": {"type": "long"}
        }}}""".trimIndent()
        )
        log.info("Created index: $index")
    }
}
