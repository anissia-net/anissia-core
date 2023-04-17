package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.NewAnimeCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface NewAnime {
    fun handle(cmd: NewAnimeCommand, session: Session): ResultWrapper<Long>
}
