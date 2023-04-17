package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.NewCaptionCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface NewCaption {
    fun handle(cmd: NewCaptionCommand, session: Session): ResultWrapper<Unit>
}
