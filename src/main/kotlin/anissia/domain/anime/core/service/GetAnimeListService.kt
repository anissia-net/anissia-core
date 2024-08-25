package anissia.domain.anime.core.service

import anissia.domain.anime.core.model.AnimeItem
import anissia.domain.anime.core.model.GetAnimeListCommand
import anissia.domain.anime.core.ports.inbound.GetAnimeList
import anissia.domain.anime.core.ports.outbound.AnimeRepository
import anissia.infrastructure.common.As
import anissia.infrastructure.service.ElasticsearchService
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

                        if (keywords.isNotEmpty() || genres.isNotEmpty()) {
                            putArray("must").apply {
                                keywords.forEach {
                                    addObject().apply {
                                        putObject("wildcard").apply { put("subject", "*$it*") }
                                    }
                                }
                                genres.forEach {
                                    addObject().apply {
                                        putObject("match").apply {
                                            put("genres", it)
                                        }
                                    }
                                }
                            }
                        }

                        if (translators.isNotEmpty()) {
                            putArray("should").apply {
                                translators.forEach {
                                    addObject().apply {
                                        putObject("match").apply {
                                            put("translators", it)
                                        }
                                    }
                                }
                            }
                        }

                        if (end) {
                            putArray("filter").apply {
                                addObject().apply {
                                    putObject("match").apply {
                                        put("status", "END")
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
            val hits = res.entity.content.bufferedReader().use { As.OBJECT_MAPPER.readTree(it) }["hits"]
            val result = PageImpl<Long>(hits["hits"].map { it["_id"].asLong() }, PageRequest.of(page, 30), hits["total"]["value"].asLong())

            log.info("anime search $keywords $genres $translators $end ${result.totalElements}")

            return As.replacePage(result, animeRepository.findAllByAnimeNoInOrderByAnimeNoDesc(result.content).map { AnimeItem(it) })
        } else {
            return animeRepository.findAllByOrderByAnimeNoDesc(PageRequest.of(page, 30)).map { AnimeItem(it) }
        }
    }

}
