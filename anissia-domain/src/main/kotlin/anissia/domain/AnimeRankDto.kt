package anissia.domain

data class AnimeRankDto (
        var animeNo: Long = 0,
        var subject: String = "",
        var hit: Long = 0,
        var rank: Int = 0
) {
        constructor(animeNo: Long, subject: String, hit: Long): this(animeNo, subject, hit, 0);
}
