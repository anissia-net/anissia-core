package anissia.domain.board.service

import anissia.domain.board.command.DeletePostCommand
import anissia.domain.board.command.EditPostCommand
import anissia.domain.board.command.NewPostCommand
import anissia.domain.session.model.SessionItem
import anissia.shared.ApiResponse

interface PostService{
    fun add(cmd: NewPostCommand, sessionItem: SessionItem): ApiResponse<Unit>
    fun edit(cmd: EditPostCommand, sessionItem: SessionItem): ApiResponse<Unit>
    fun delete(cmd: DeletePostCommand, sessionItem: SessionItem): ApiResponse<Unit>
}
