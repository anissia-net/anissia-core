package anissia.domain.board.service

import anissia.domain.board.model.EditTopicCommand
import anissia.domain.board.repository.BoardPostRepository
import anissia.domain.board.repository.BoardTopicRepository
import anissia.domain.session.model.Session
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
                    ?.also { boardPostRepository.save(it.apply { edit(cmd.content) }) }
                    ?: ResultWrapper.fail("권한이 없거나 존재하지 않는 글입니다.", null)
                boardTopicRepository.save(node.apply { edit(cmd.topic) })
                ResultWrapper.ok()
            }
            ?: ResultWrapper.fail("권한이 없거나 존재하지 않는 글입니다.")
    }

}
