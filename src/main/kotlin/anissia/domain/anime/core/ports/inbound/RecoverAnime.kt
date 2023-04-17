package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.RecoverAnimeCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface RecoverAnime {
    fun handle(cmd: RecoverAnimeCommand, session: Session): ResultWrapper<Long>
}
