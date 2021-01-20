package anissia.elasticsearch.repository

import anissia.elasticsearch.domain.AnimeDocument
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface AnimeDocumentRepository : ElasticsearchRepository<AnimeDocument, Long> {
}


//
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
