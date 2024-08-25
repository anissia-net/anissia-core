package anissia.domain.anime.core.service

import anissia.domain.anime.core.Anime
import anissia.domain.anime.core.model.UpdateAnimeDocumentCommand
import anissia.domain.anime.core.ports.inbound.UpdateAnimeDocument
import anissia.domain.anime.core.ports.outbound.AnimeCaptionRepository
import anissia.domain.anime.core.ports.outbound.AnimeRepository
import anissia.infrastructure.service.ElasticsearchService
import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.client.Request
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateAnimeDocumentService(
    private val animeRepository: AnimeRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val elasticsearch: ElasticsearchService,
    private val objectMapper: ObjectMapper,
): UpdateAnimeDocument {
    @Transactional
    override fun handle(cmd: UpdateAnimeDocumentCommand) {
        if (cmd.isDelete) {
            handle(Anime(animeNo = cmd.animeNo), true)
        } else {
            animeRepository.findByIdOrNull(cmd.animeNo)
                ?.let { handle(it) }
                ?: handle(Anime(animeNo = cmd.animeNo), true)
        }
    }

    @Transactional
    override fun handle(anime: Anime, isDelete: Boolean) {
        if (isDelete) {
            elasticsearch.open().use {
                it.performRequest(Request("DELETE", "/anissia_anime/_doc/${anime.animeNo}"))
            }
        } else {
            elasticsearch.open().use {
                it.performRequest(
                    Request("PUT", "/anissia_anime/_doc/${anime.animeNo}")
                        .apply { setJsonEntity(objectMapper.writeValueAsString(mapOf(
                            "animeNo" to anime.animeNo,
                            "week" to anime.week,
                            "subject" to anime.subject + " " + anime.originalSubject,
                            "status" to anime.status.name,
                            "genres" to anime.genres.split(",".toRegex()),
                            "translators" to animeCaptionRepository.findAllTranslatorByAnimeNo(anime.animeNo),
                            "endDate" to anime.endDate.replace("-", "").run { if (isEmpty()) 0L else toLong() }
                        ))) }
                )
            }
        }
    }

    @Transactional
    override fun createIndex() {
        elasticsearch.open().use {
            val req = Request("PUT", "/anissia_anime")
            req.setJsonEntity("""{"mappings":{"properties": {
                "animeNo": {"type": "long","store": true},
                "week": {"type": "keyword"},
                "subject": {"type": "text"},
                "genres": {"type": "keyword"},
                "status": {"type": "keyword"},
                "translators": {"type": "keyword"},
                "endDate": {"type": "long"}
            }}}""")
            it.performRequest(req)
        }
    }
}
