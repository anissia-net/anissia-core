package anissia.domain.board.core.ports.inbound

import anissia.domain.board.core.model.NewTopicCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface NewTopic {
    fun handle(cmd: NewTopicCommand, session: Session): ResultWrapper<Long>
}
