package anissia.domain.board.core.service

import anissia.domain.board.core.model.DeletePostCommand
import anissia.domain.board.core.ports.inbound.DeletePost
import anissia.domain.board.core.ports.outbound.BoardPostRepository
import anissia.domain.board.core.ports.outbound.BoardTopicRepository
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePostService(
    private val boardPostRepository: BoardPostRepository,
    private val boardTopicRepository: BoardTopicRepository,
): DeletePost {
    @Transactional
    override fun handle(cmd: DeletePostCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()
        session.validateLogin()

        return boardPostRepository
            .findByIdOrNull(cmd.postNo)
            ?.takeIf { !it.root && (it.an == session.an || session.isRoot) }
            ?.let {
                boardPostRepository.delete(it)
                boardTopicRepository.updatePostCount(it.topicNo)
                ResultWrapper.ok()
            }
            ?: ResultWrapper.fail("권한이 없거나 존재하지 않는 글입니다.")
    }
}
