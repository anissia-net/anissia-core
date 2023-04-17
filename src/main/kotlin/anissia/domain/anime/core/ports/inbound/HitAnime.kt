package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.HitAnimeCommand
import anissia.domain.session.core.model.Session

interface HitAnime {
    fun handle(cmd: HitAnimeCommand, session: Session)
}
