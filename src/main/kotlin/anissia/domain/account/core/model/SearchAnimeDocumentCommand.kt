package anissia.domain.account.core.model

import org.springframework.data.domain.Pageable

class SearchAnimeDocumentCommand(
    val keywords: List<String>,
    val genres: List<String>,
    val translators: List<String>,
    val end: Boolean,
    val pageable: Pageable
)
