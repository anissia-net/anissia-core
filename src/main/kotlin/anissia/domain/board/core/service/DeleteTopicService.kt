package anissia.domain.board.core.service

import anissia.domain.board.core.model.DeleteTopicCommand
import anissia.domain.board.core.ports.inbound.DeleteTopic
import anissia.domain.board.core.ports.outbound.BoardPostRepository
import anissia.domain.board.core.ports.outbound.BoardTopicRepository
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteTopicService(
    private val boardPostRepository: BoardPostRepository,
    private val boardTopicRepository: BoardTopicRepository,
): DeleteTopic {
    @Transactional
    override fun handle(cmd: DeleteTopicCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()
        session.validateLogin()

        return boardTopicRepository
            .findByIdOrNull(cmd.topicNo)
            ?.takeIf { it.an == session.an || session.isRoot }
            ?.let {
                boardPostRepository.deleteAllByTopicNo(cmd.topicNo)
                boardTopicRepository.delete(it)
                ResultWrapper.ok()
            }
            ?: ResultWrapper.fail("권한이 없거나 존재하지 않는 글입니다.")
    }

}
