package anissia.domain.board.core.service

import anissia.domain.board.core.BoardPost
import anissia.domain.board.core.model.NewPostCommand
import anissia.domain.board.core.ports.inbound.NewPost
import anissia.domain.board.core.ports.outbound.BoardPostRepository
import anissia.domain.board.core.ports.outbound.BoardTickerRepository
import anissia.domain.board.core.ports.outbound.BoardTopicRepository
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NewPostService(
    private val boardPostRepository: BoardPostRepository,
    private val boardTickerRepository: BoardTickerRepository,
    private val boardTopicRepository: BoardTopicRepository,
): NewPost {
    @Transactional
    override fun handle(cmd: NewPostCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()
        session.validateLogin()

        return boardTopicRepository
            .findByIdOrNull(cmd.topicNo)
            ?.takeIf { permission(it.ticker, session) }
            ?.let {
                boardPostRepository.saveAndFlush(
                    BoardPost(
                        topicNo = cmd.topicNo,
                        content = cmd.content,
                        an = session.an,
                    )
                )
                boardTopicRepository.updatePostCount(cmd.topicNo)
                ResultWrapper.ok()
            }
            ?: ResultWrapper.fail("권한이 없거나 존재하지 않는 글 혹은 게시판입니다.")
    }


    private fun permission(ticker: String, session: Session): Boolean =
        boardTickerRepository.findByIdOrNull(ticker)?.run {
            writePostRoles.isEmpty() || session.roles.any { it in writePostRoles }
        } ?: false
}
