package anissia.domain.anime.service

import anissia.domain.anime.model.AnimeItem
import anissia.domain.anime.model.GetAnimeCommand
import anissia.domain.session.model.Session

interface GetAnime {
    fun handle(cmd: GetAnimeCommand, session: Session): AnimeItem
}
