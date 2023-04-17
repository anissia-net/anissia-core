package anissia.domain.board.core.ports.inbound

import anissia.domain.board.core.model.DeletePostCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface DeletePost{
    fun handle(cmd: DeletePostCommand, session: Session): ResultWrapper<Unit>
}
