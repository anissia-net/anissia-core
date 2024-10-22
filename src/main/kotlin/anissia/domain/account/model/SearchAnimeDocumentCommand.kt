package anissia.domain.account.model

import org.springframework.data.domain.Pageable

class SearchAnimeDocumentCommand(
    val keywords: List<String>,
    val genres: List<String>,
    val translators: List<String>,
    val end: Boolean,
    val pageable: Pageable
)
