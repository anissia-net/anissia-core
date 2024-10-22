package anissia.domain.anime.service

import anissia.domain.anime.model.HitAnimeCommand
import anissia.domain.session.model.Session

interface HitAnime {
    fun handle(cmd: HitAnimeCommand, session: Session)
}
