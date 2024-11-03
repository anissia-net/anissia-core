package anissia.domain.anime.command

class GetAnimeRankCommand(
    val type: String
) {
    fun validate() {
        when (type) {
            "week", "quarter", "year" -> {}
            else -> throw IllegalArgumentException("invalid type")
        }
    }
}
