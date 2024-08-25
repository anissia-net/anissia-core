package anissia.domain.anime.core.service

import anissia.domain.account.core.model.SearchAnimeDocumentCommand
import anissia.domain.anime.core.model.AnimeItem
import anissia.domain.anime.core.model.GetAnimeListCommand
import anissia.domain.anime.core.ports.inbound.GetAnimeList
//import anissia.domain.anime.core.ports.outbound.AnimeDocumentRepository
import anissia.domain.anime.core.ports.outbound.AnimeRepository
import anissia.infrastructure.common.As
import anissia.infrastructure.service.ElasticsearchService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class GetAnimeListService(
    private val animeRepository: AnimeRepository,
    private val elasticsearch: ElasticsearchService,
): GetAnimeList {
    private val log = As.logger<GetAnimeList>()

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

            val req = As.toJsonString(As.OBJECT_MAPPER.createObjectNode().apply {

                putObject("query").apply {
                    putObject("bool").apply {
                        put("minimum_should_match", "100%")

                        if (keywords.isNotEmpty()) {
                            putArray("must").apply {
                                keywords.forEach {
                                    addObject().apply {
                                        putObject("wildcard").apply { put("subject", "*$it*") }
                                    }
                                }
                            }
                        }

                        if (genres.isNotEmpty() || translators.isNotEmpty() || end) {
                            putArray("filter").apply {
                                putObject("bool").apply {

                                    if (genres.isNotEmpty()) {

                                        //filter


                                        addObject().apply {
                                            putObject("genres").apply {
                                                put("genres", genres.joinToString(" "))
                                                put("minimum_should_match", "100%")
                                            }
                                        }
                                    }

                                    if (translators.isNotEmpty()) {
                                        add(As.OBJECT_MAPPER.createObjectNode().putObject("translators").apply {
                                            put("translators", translators.joinToString(" "))
                                        })
                                    }

                                    if (end) {
                                        add(As.OBJECT_MAPPER.createObjectNode().putObject("match").apply {
                                            put("status", "END")
                                        })
                                    }
                                }
                            }
                        }
                    }
                }
                // 종료된 애니만 검색
                if (end) {
                    putArray("sort").apply {
                        addObject().apply {
                            putObject("endDate").apply {
                                put("order", "desc")
                            }
                        }
                    }
                }
                put("from", page * 30)
                put("size", 30)
            })

            log.info(req)

            val res = elasticsearch.request("POST", "/anissia_anime/_search", req)

            val responseBody = res.entity.content.bufferedReader().use { it.readText() }
            println("Response: $responseBody")

            log.info("anime search $keywords $genres $translators $end")

            //log.info("anime search $keywords $genres $translators $end ${res.totalElements}")

            return animeRepository.findAllByOrderByAnimeNoDesc(PageRequest.of(page, 30)).map { AnimeItem(it) }
        } else {
            return animeRepository.findAllByOrderByAnimeNoDesc(PageRequest.of(page, 30)).map { AnimeItem(it) }
        }
    }

}
