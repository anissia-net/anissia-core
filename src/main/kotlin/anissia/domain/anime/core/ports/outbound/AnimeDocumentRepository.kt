package anissia.domain.anime.core.ports.outbound

import anissia.domain.anime.core.AnimeDocument
import anissia.infrastructure.common.As
import co.elastic.clients.elasticsearch._types.SortOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHitSupport
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface AnimeDocumentRepository : ElasticsearchRepository<AnimeDocument, Long>, AnimeDocumentRepositoryCustom

interface AnimeDocumentRepositoryCustom {
    fun findAllAnimeNoForAnimeSearch(keywords: List<String>, genres: List<String>, translators: List<String>, end: Boolean, pageable: Pageable): Page<Long>
}

class AnimeDocumentRepositoryCustomImpl(
    private val operations: ElasticsearchOperations
): AnimeDocumentRepositoryCustom {

    var log = As.logger<AnimeDocumentRepositoryCustomImpl>()
    override fun findAllAnimeNoForAnimeSearch(keywords: List<String>, genres: List<String>, translators: List<String>, end: Boolean, pageable: Pageable): Page<Long> {
        val query = NativeQueryBuilder().withPageable(pageable)

        if (keywords.isNotEmpty()) {
            query.withQuery { q ->
                q.wildcard { it.field("subject").wildcard(keywords.joinToString("*", "*", "*")) }
            }
        }

        if (genres.isNotEmpty() || translators.isNotEmpty() || end) {
            query.withFilter { filter ->
                filter.bool { bq ->
                    bq.minimumShouldMatch("100%")
                    if (genres.isNotEmpty()) {
                        bq.filter { fs -> fs.match { it.field("genres").query(genres.joinToString(" ")).minimumShouldMatch("100%") } }
                    }
                    if (translators.isNotEmpty()) {
                        bq.filter { fs -> fs.match { it.field("translators").query(translators.joinToString(" ")) } }
                    }
                    if (end) {
                        bq.filter { fs -> fs.match { it.field("status").query("END") } }
                    }
                    bq
                }
            }
            if (end) {
                query.withSort { f -> f.field { fn -> fn.field("endDate").order(SortOrder.Desc) } }
            }
        }

        return SearchHitSupport.searchPageFor(operations.search(query.build(), AnimeDocument::class.java), pageable).map { it.content.animeNo }
    }
}
