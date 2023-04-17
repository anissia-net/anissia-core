package anissia.domain.anime.core.model

class GetCaptionListByAnimeNoCommand(
    val animeNo: Long
) {
    fun validate() {
        require(animeNo > 0) { "animeNo 는 0 이상이어야 합니다." }
    }
}
