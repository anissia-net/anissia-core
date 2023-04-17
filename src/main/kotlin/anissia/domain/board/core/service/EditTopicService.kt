package anissia.domain.board.core.service

import anissia.domain.board.core.model.EditTopicCommand
import anissia.domain.board.core.ports.inbound.EditTopic
import anissia.domain.board.core.ports.outbound.BoardPostRepository
import anissia.domain.board.core.ports.outbound.BoardTopicRepository
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EditTopicService(
    private val boardPostRepository: BoardPostRepository,
    private val boardTopicRepository: BoardTopicRepository,
): EditTopic {
    @Transactional
    override fun handle(cmd: EditTopicCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()
        session.validateLogin()

        return boardTopicRepository
            .findByIdOrNull(cmd.topicNo)
            ?.takeIf { it.an == session.an }
            ?.let { node ->
                boardPostRepository
                    .findWithAccountByTopicNoAndRootIsTrue(cmd.topicNo)
                    ?.also { boardPostRepository.save(it.apply { content = cmd.content }) }
                    ?: ResultWrapper.fail("권한이 없거나 존재하지 않는 글입니다.", null)
                boardTopicRepository.save(node.apply { topic = cmd.topic })
                ResultWrapper.ok()
            }
            ?: ResultWrapper.fail("권한이 없거나 존재하지 않는 글입니다.")
    }

}
