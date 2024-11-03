package anissia.domain.board.service

import anissia.domain.board.command.NewTopicCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface NewTopic {
    fun handle(cmd: NewTopicCommand, session: Session): ResultWrapper<Long>
}
