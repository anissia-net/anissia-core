package anissia.domain.board.core.ports.inbound

import anissia.domain.board.core.model.DeleteTopicCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface DeleteTopic {
    fun handle(cmd: DeleteTopicCommand, session: Session): ResultWrapper<Unit>
}
