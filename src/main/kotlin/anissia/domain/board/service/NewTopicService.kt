package anissia.domain.board.service

import anissia.domain.board.BoardPost
import anissia.domain.board.BoardTopic
import anissia.domain.board.command.NewTopicCommand
import anissia.domain.board.repository.BoardPostRepository
import anissia.domain.board.repository.BoardTickerRepository
import anissia.domain.board.repository.BoardTopicRepository
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NewTopicService(
    private val boardPostRepository: BoardPostRepository,
    private val boardTickerRepository: BoardTickerRepository,
    private val boardTopicRepository: BoardTopicRepository,
): NewTopic {
    @Transactional
    override fun handle(cmd: NewTopicCommand, session: Session): ResultWrapper<Long> {
        if (!session.isLogin) {
            return ResultWrapper.fail("로그인이 필요합니다.", 0)
        }

        return cmd.ticker
            .takeIf { permission(it, session) }
            ?.let {
                val topic = boardTopicRepository.saveAndFlush(
                    BoardTopic.create(
                        ticker = cmd.ticker,
                        topic = cmd.topic,
                        an = session.an,
                    )
                )
                boardPostRepository.saveAndFlush(
                    BoardPost.createRootPost(
                        topicNo = topic.topicNo,
                        content = cmd.content,
                        an = session.an,
                    )
                )
                ResultWrapper.ok(topic.topicNo)
            }
            ?: ResultWrapper.fail("권한이 없습니다.", -1)
    }

    private fun permission(ticker: String, session: Session): Boolean =
        boardTickerRepository.findByIdOrNull(ticker)?.run {
            writeTopicRoles.isEmpty() || session.roles.any { it in writeTopicRoles }
        } ?: false
}
