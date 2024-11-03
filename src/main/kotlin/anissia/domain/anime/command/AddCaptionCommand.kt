package anissia.domain.anime.command

class AddCaptionCommand(
    val animeNo: Long,
) {
    fun validate() {
        require(animeNo > 0) { "animeNo 는 0 이상이어야 합니다." }
    }
}
