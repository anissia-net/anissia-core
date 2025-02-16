package anissia.domain.anime.model


import anissia.domain.anime.Anime

class AnimeItem(
    val animeNo: Long = 0,
    val status: String = "",
    val week: String = "",
    val time: String = "",
    val subject: String = "",
    val originalSubject: String = "",
    val captionCount: Int = 0,
    val genres: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val website: String = "",
    val twitter: String = "",
    val note: String = "",
    var agendaNo: Long = 0,
    val captions: List<AnimeCaptionItem> = emptyList()
) {
    constructor(anime: Anime, includeCaption: Boolean = false): this(
        animeNo = anime.animeNo,
        status = anime.status.toString(),
        week = anime.week,
        time = anime.time,
        subject = anime.subject,
        originalSubject = anime.originalSubject,
        captionCount = anime.captionCount,
        genres = anime.genres,
        startDate = anime.startDate,
        endDate = anime.endDate,
        website = anime.website,
        twitter = anime.twitter,
        note = anime.note,
        captions = if (includeCaption) anime.captions.map { AnimeCaptionItem(it) } else emptyList()
    )
}

