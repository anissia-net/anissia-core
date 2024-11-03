package anissia.domain.anime.model

class GetMyListCaptionCommand(
    val active: Int,
    val page: Int
) {
    fun validate() {
        require(active in 0..1) { "active 는 0 또는 1 이어야 합니다." }
        require(page >= 0) { "page 는 0 이상이어야 합니다." }
    }
}
