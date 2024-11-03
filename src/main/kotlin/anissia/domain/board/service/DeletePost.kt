package anissia.domain.board.service

import anissia.domain.board.command.DeletePostCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface DeletePost{
    fun handle(cmd: DeletePostCommand, session: Session): ResultWrapper<Unit>
}
