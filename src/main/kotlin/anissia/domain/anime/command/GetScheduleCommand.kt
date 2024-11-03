package anissia.domain.anime.command

class GetScheduleCommand(
    val week: String,
    val useCache: Boolean = true,
) {
    fun validate() {
        when (week) {
            "0", "1", "2", "3", "4", "5", "6", "7", "8" -> {}
            else -> throw IllegalArgumentException("invalid week")
        }
    }
}
