package anissia.elasticsearch.repository

import anissia.elasticsearch.domain.AnimeDocument
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface AnimeDocumentRepository : ElasticsearchRepository<AnimeDocument, Long> {

    fun findAllBySubjectLikeAndGenresIn(subject: String, genres: List<String>, pageable: Pageable): Page<AnimeDocument>
    fun findAllBySubjectLike(subject: String, pageable: Pageable): Page<AnimeDocument>

}
