package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.EditAnimeCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface EditAnime {
    fun handle(cmd: EditAnimeCommand, session: Session): ResultWrapper<Long>
}
