package anissia.domain.anime.service

import anissia.domain.anime.model.AnimeItem
import anissia.domain.anime.model.GetAnimeListCommand
import anissia.domain.anime.repository.AnimeRepository
import anissia.infrastructure.common.As
import anissia.infrastructure.service.ElasticsearchService
import com.fasterxml.jackson.core.io.JsonStringEncoder
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class GetAnimeListService(
    private val animeRepository: AnimeRepository,
    private val elasticsearch: ElasticsearchService,
): GetAnimeList {
    private val log = As.logger<GetAnimeList>()
    private val jsonStringEncoder = JsonStringEncoder.getInstance()
    private val mapper = As.OBJECT_MAPPER

    override fun handle(cmd: GetAnimeListCommand): Page<AnimeItem> {
        val q = cmd.q
        val page = cmd.page

        if (q.isNotBlank()) {
            val keywords = ArrayList<String>()
            val genres = ArrayList<String>()
            val translators = ArrayList<String>()
            val end = q.indexOf("/완결") != -1

            q.lowercase(Locale.getDefault())
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
                                    addObject().apply { putObject("bool").apply {
                                        putArray("should").apply {
                                            translators.forEach {
                                                addObject().apply { putObject("match").apply { put("translators", it) } }
                                            }
                                        }
                                        put("minimum_should_match", "1")
                                    } }
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

//            """{
//                "_source": false,
//                "query": {
//                    "bool": {
//                        "minimum_should_match": "100%",
//                        "must": [
//                            ${keywords.joinToString(",") { """{"wildcard": {"subject": "*${it.es}*"}}""" }},
//                            ${genres.joinToString(",") { """{"match": {"genres": "${it.es}"}}""" }}
//                        ],
//                        "filter": [
//                            ${translators.joinToString(",") { """{"bool": {"should": [${"""{"match": {"translators": "${it.es}"}}"""}], "minimum_should_match": "1"}}""" }},
//                            ${if (end) """{"match": {"status": "END"}}""" else ""}
//                        ]
//                    }
//                },
//                ${if (end) """{"sort": [{"endDate": {"order": "desc"}}]}""" else ""}
//                "from": ${page * 30},
//                "size": 30
//            }"""

            log.info(req)

            val res = elasticsearch.request("POST", "/anissia_anime/_search", req)
            val hits = res.entity.content.bufferedReader().use { mapper.readTree(it) }["hits"]
            val result = PageImpl<Long>(hits["hits"].map { it["_id"].asLong() }, PageRequest.of(page, 30), hits["total"]["value"].asLong())

            log.info("anime search $keywords $genres $translators $end ${result.totalElements}")

            return As.replacePage(result, animeRepository.findAllByAnimeNoInOrderByAnimeNoDesc(result.content).map { AnimeItem(it) })
        } else {
            return animeRepository.findAllByOrderByAnimeNoDesc(PageRequest.of(page, 30)).map { AnimeItem(it) }
        }
    }

    // encoding string
    //private val String.es: CharArray get() = jsonStringEncoder.quoteAsString(this)

}
