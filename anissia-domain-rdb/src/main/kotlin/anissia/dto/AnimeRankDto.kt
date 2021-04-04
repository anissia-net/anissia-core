package anissia.dto

data class AnimeRankDto (
        var animeNo: Long = 0,
        var subject: String = "",
        var hit: Long = 0,
        var rank: Int = 0,
        var diff: Int? = null
) {
        constructor(animeNo: Long, subject: String?, hit: Long): this(animeNo, subject ?: "", hit, 0, null)

        /** exist anime (is not removed) */
        val exist get() = subject != ""
}
