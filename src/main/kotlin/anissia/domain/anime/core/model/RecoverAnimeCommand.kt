package anissia.domain.anime.core.model

class RecoverAnimeCommand(
    val agendaNo: Long = 0,
) {
    fun validate() {
        require(agendaNo > 0) { "agendaNo 는 0 이상이어야 합니다." }
    }
}
