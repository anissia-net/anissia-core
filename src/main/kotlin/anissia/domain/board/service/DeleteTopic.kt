package anissia.domain.board.service

import anissia.domain.board.core.model.DeleteTopicCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface DeleteTopic {
    fun handle(cmd: DeleteTopicCommand, session: Session): ResultWrapper<Unit>
}
