package anissia.domain.board.core.ports.inbound

import anissia.domain.board.core.model.EditPostCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface EditPost{
    fun handle(cmd: EditPostCommand, session: Session): ResultWrapper<Unit>
}
