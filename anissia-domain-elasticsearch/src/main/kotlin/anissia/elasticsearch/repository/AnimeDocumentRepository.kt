package anissia.elasticsearch.repository

import anissia.elasticsearch.domain.AnimeDocument
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface AnimeDocumentRepository : ElasticsearchRepository<AnimeDocument, Long> {
    fun findAllBySubjectLikeAndGenresLike(subject: String, genres: String, pageable: Pageable): Page<AnimeDocument>
}
