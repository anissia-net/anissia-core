package anissia.domain.anime.command

class GetRecentListCaptionCommand(
    val page: Int
) {
    fun validate() {
        require(page > -2) { "잘못된 pageNo" }
    }
}
