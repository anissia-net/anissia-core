package anissia.domain.board.service

import anissia.domain.board.core.model.EditTopicCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper
import org.springframework.transaction.annotation.Transactional

interface EditTopic {
    @Transactional
    fun handle(cmd: EditTopicCommand, session: Session): ResultWrapper<Unit>
}
