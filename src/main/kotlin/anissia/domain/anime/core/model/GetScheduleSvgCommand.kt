package anissia.domain.anime.core.model

class GetScheduleSvgCommand(
    val width: String,
    val color: String
) {
    fun validate() {
        if (!width.matches(Regex("\\d{3}"))) {
            throw IllegalArgumentException("invalid width")
        }

        if (!color.matches(Regex("[a-f\\d]{36}"))) {
            throw IllegalArgumentException("invalid color")
        }
    }
}
