package anissia.domain.anime.model

import anissia.domain.anime.Anime

class AnimeScheduleItem (
    val week: String,
    val animeNo: Long,
    val status: String,
    val time: String,
    val subject: String,
    val originalSubject: String,
    val genres: String,
    val captionCount: Int,
    val startDate: String,
    val endDate: String,
    val website: String,
    val twitter: String,
) {
    constructor(anime: Anime): this(
        week = anime.week,
        animeNo = anime.animeNo,
        status = anime.status.toString(),
        time = if (!anime.week.matches("7|8".toRegex())) anime.time else anime.startDate,
        subject = anime.subject,
        originalSubject = anime.originalSubject,
        genres = anime.genres,
        captionCount = anime.captionCount,
        startDate = anime.startDate,
        endDate = anime.endDate,
        website = anime.website,
        twitter = anime.twitter
    )
}
