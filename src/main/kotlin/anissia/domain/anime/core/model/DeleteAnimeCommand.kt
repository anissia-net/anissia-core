package anissia.domain.anime.core.model

class DeleteAnimeCommand(
    val animeNo: Long = 0,
) {
    fun validate() {
        require(animeNo > 0) { "animeNo 는 0 이상이어야 합니다." }
    }
}
