package anissia.domain.board.service

import anissia.domain.board.model.DeletePostCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface DeletePost{
    fun handle(cmd: DeletePostCommand, session: Session): ResultWrapper<Unit>
}
