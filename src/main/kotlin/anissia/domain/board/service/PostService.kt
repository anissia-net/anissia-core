package anissia.domain.board.service

import anissia.domain.board.command.DeletePostCommand
import anissia.domain.board.command.EditPostCommand
import anissia.domain.board.command.NewPostCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface PostService{
    fun add(cmd: NewPostCommand, session: Session): ResultWrapper<Unit>
    fun edit(cmd: EditPostCommand, session: Session): ResultWrapper<Unit>
    fun delete(cmd: DeletePostCommand, session: Session): ResultWrapper<Unit>
}
