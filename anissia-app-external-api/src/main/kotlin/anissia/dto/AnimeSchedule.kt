package anissia.dto

import anissia.domain.Anime

data class AnimeSchedule (
    var animeNo: Long = 0,
    var status: String = "",
    var week: String = "",
    var time: String = "",
    var subject: String = "",
    var genres: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var website: String = "",
) {
    constructor(anime: Anime): this(
        animeNo = anime.animeNo,
        status = anime.status.toString(),
        week = anime.week,
        time = if (!anime.week.matches("7|8".toRegex())) anime.time else anime.startDate,
        subject = anime.subject,
        genres = anime.genres,
        startDate = anime.startDate,
        endDate = anime.endDate,
        website = anime.website
    )
}
