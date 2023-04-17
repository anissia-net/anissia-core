package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.EditCaptionCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface EditCaption {
    fun handle(cmd: EditCaptionCommand, session: Session): ResultWrapper<Unit>
}
