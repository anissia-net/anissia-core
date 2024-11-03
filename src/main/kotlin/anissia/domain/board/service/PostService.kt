package anissia.domain.board.service

import anissia.domain.board.command.DeletePostCommand
import anissia.domain.board.command.EditPostCommand
import anissia.domain.board.command.NewPostCommand
import anissia.domain.session.model.SessionItem
import anissia.shared.ResultWrapper

interface PostService{
    fun add(cmd: NewPostCommand, sessionItem: SessionItem): ResultWrapper<Unit>
    fun edit(cmd: EditPostCommand, sessionItem: SessionItem): ResultWrapper<Unit>
    fun delete(cmd: DeletePostCommand, sessionItem: SessionItem): ResultWrapper<Unit>
}
