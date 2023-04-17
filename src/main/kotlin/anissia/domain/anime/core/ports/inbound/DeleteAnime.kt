package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.DeleteAnimeCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface DeleteAnime {
    fun handle(cmd: DeleteAnimeCommand, session: Session): ResultWrapper<Unit>
}
