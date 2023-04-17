package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.DeleteCaptionCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface DeleteCaption {
    fun handle(cmd: DeleteCaptionCommand, session: Session): ResultWrapper<Unit>
}
