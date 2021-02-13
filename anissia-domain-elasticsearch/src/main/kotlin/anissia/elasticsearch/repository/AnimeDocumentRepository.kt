package anissia.elasticsearch.repository

import anissia.elasticsearch.domain.AnimeDocument
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHitSupport
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface AnimeDocumentRepository : ElasticsearchRepository<AnimeDocument, Long>, AnimeDocumentRepositoryCustom

interface AnimeDocumentRepositoryCustom {
    fun findAllAnimeNoForAnimeSearch(keywords: List<String>, genres: List<String>, translators: List<String>, pageable: Pageable): Page<Long>
}

class AnimeDocumentRepositoryCustomImpl(
    private val operations: ElasticsearchOperations
): AnimeDocumentRepositoryCustom {
    override fun findAllAnimeNoForAnimeSearch(keywords: List<String>, genres: List<String>, translators: List<String>, pageable: Pageable): Page<Long> {
        val query = NativeSearchQueryBuilder().withPageable(pageable)

        if (keywords.isNotEmpty()) {
            query.withQuery(QueryBuilders.wildcardQuery("subject", keywords.joinToString("*", "*", "*")))
        }
        if (genres.isNotEmpty()) {
            query.withFilter(QueryBuilders.termsQuery("genres", *genres.toTypedArray()))
        }
        if (translators.isNotEmpty()) {
            query.withFilter(QueryBuilders.termsQuery("translators", *translators.toTypedArray()))
        }

        return SearchHitSupport.searchPageFor(operations.search(query.build(), AnimeDocument::class.java), pageable).map { it.content.animeNo }
    }
}
