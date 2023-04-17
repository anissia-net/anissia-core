package anissia.domain.board.core.ports.inbound

import anissia.domain.board.core.model.NewPostCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface NewPost {
    fun handle(cmd: NewPostCommand, session: Session): ResultWrapper<Unit>
}
