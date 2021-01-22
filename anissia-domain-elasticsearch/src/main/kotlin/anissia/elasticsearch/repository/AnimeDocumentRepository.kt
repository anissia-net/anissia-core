package anissia.elasticsearch.repository

import anissia.elasticsearch.domain.AnimeDocument
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import java.util.*
import java.util.stream.StreamSupport

interface AnimeDocumentRepository : ElasticsearchRepository<AnimeDocument, Long>/*, AnimeDocumentRepositoryCustom*/ {

    fun findAllBySubjectLikeAndGenresIn(subject: String, genres: List<String>, pageable: Pageable): Page<AnimeDocument>
    fun findAllBySubjectLike(subject: String, pageable: Pageable): Page<AnimeDocument>

}

//interface AnimeDocumentRepositoryCustom {
//    fun findAllForAnimeSearch(subject: String, genres: List<String>, pageable: Pageable);
//}
//
//class AnimeDocumentRepositoryCustomImpl(
//    private val operations: ElasticsearchOperations
//): AnimeDocumentRepositoryCustom {
//    override fun findAllForAnimeSearch(keywords: List<String>, genres: List<String>, pageable: Pageable) {
//
//        val qb = NativeSearchQueryBuilder()
//        if (keywords.isNotEmpty()) {
//            qb.withQuery(QueryBuilders.matchQuery("subject", keywords.joinToString("*", "*", "*")))
//        }
//        if (genres.isNotEmpty()) {
//            qb.withQuery(QueryBuilders.matchQuery("genres", genres.joinToString("*", "*", "*")))
//        }
//
//        operations.searchForStream(
//            qb.build(),
//            AnimeDocument::class.java
//        )
//    }
//}

//interface TagDocumentRepositoryCustom {
//    fun getIdsByKeywords(keywords: String): Stream<SearchHit<TagDocument>>
//}
//
//class TagDocumentRepositoryCustomImpl(
//    private val operations: ElasticsearchOperations
//) : TagDocumentRepositoryCustom {
//    override fun getIdsByKeywords(keywords: String): Stream<SearchHit<TagDocument>> =
//        StreamSupport.stream(
//            Spliterators.spliteratorUnknownSize(
//            operations.searchForStream(
//                NativeSearchQueryBuilder()
//                    .withQuery(QueryBuilders.matchQuery("keyword", keywords))
//                    .withFields("content.id")
//                    .build(),
//                TagDocument::class.java
//            ),
//            Spliterator.ORDERED), false)
//}