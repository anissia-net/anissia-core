package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.AnimeItem
import anissia.domain.anime.core.model.GetAnimeCommand
import anissia.domain.session.core.model.Session

interface GetAnime {
    fun handle(cmd: GetAnimeCommand, session: Session): AnimeItem
}
