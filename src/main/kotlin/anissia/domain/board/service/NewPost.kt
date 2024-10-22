package anissia.domain.board.service

import anissia.domain.board.core.model.NewPostCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface NewPost {
    fun handle(cmd: NewPostCommand, session: Session): ResultWrapper<Unit>
}
