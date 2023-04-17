package anissia.domain.anime.core.model

class GetCaptionRecentCommand(
    val page: Int
) {
    fun validate() {
        require(page > -2) { "잘못된 pageNo" }
    }
}
