package anissia.domain.board.service

import anissia.domain.board.core.model.EditPostCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface EditPost{
    fun handle(cmd: EditPostCommand, session: Session): ResultWrapper<Unit>
}
