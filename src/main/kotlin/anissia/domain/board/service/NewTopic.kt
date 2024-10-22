package anissia.domain.board.service

import anissia.domain.board.core.model.NewTopicCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface NewTopic {
    fun handle(cmd: NewTopicCommand, session: Session): ResultWrapper<Long>
}
