package anissia.domain.board.service

import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.repository.ActivePanelRepository
import anissia.domain.board.BoardPost
import anissia.domain.board.command.DeletePostCommand
import anissia.domain.board.command.EditPostCommand
import anissia.domain.board.command.NewPostCommand
import anissia.domain.board.repository.BoardPostRepository
import anissia.domain.board.repository.BoardTickerRepository
import anissia.domain.board.repository.BoardTopicRepository
import anissia.domain.session.model.SessionItem
import anissia.shared.ResultWrapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostServiceImpl(
    private val boardPostRepository: BoardPostRepository,
    private val boardTopicRepository: BoardTopicRepository,
    private val activePanelRepository: ActivePanelRepository,
    private val boardTickerRepository: BoardTickerRepository,
): PostService {

    @Transactional
    override fun add(cmd: NewPostCommand, sessionItem: SessionItem): Mono<String> {
        cmd.validate()
        sessionItem.validateLogin()

        return boardTopicRepository
            .findByIdOrNull(cmd.topicNo)
            ?.takeIf { validAddPermission(it.ticker, sessionItem) }
            ?.let {
                boardPostRepository.saveAndFlush(
                    BoardPost.create(
                        topicNo = cmd.topicNo,
                        content = cmd.content,
                        an = sessionItem.an,
                    )
                )
                boardTopicRepository.updatePostCount(cmd.topicNo)
                )
            }
            ?: ResultWrapper.fail("권한이 없거나 존재하지 않는 글 혹은 게시판입니다.")
    }

    @Transactional
    override fun edit(cmd: EditPostCommand, sessionItem: SessionItem): Mono<String> {
        cmd.validate()
        sessionItem.validateLogin()

        return boardPostRepository
            .findByIdOrNull(cmd.postNo)
            ?.takeIf { !it.root && it.an == sessionItem.an }
            ?.let {
                boardPostRepository.save(it.apply { edit(cmd.content) })
                )
            }
            ?: ResultWrapper.fail("권한이 없거나 존재하지 않는 글입니다.")
    }

    @Transactional
    override fun delete(cmd: DeletePostCommand, sessionItem: SessionItem): Mono<String> {
        cmd.validate()
        sessionItem.validateLogin()

        return boardPostRepository
            .findByIdOrNull(cmd.postNo)
            ?.takeIf { !it.root && (it.an == sessionItem.an || sessionItem.isAdmin) }
            ?.let {
                if (it.an != sessionItem.an) {
                    activePanelRepository.save(
                        ActivePanel(
                            published = false,
                            code = "DEL",
                            an = sessionItem.an,
                            data1 = "[${sessionItem.name}]님이 댓글을 삭제했습니다.",
                            data2 = "작성자/회원번호: ${it.account?.name}/${it.an}",
                            data3 = it.content,
                        )
                    )
                }
                boardPostRepository.delete(it)
                boardTopicRepository.updatePostCount(it.topicNo)
                )
            }
            ?: ResultWrapper.fail("권한이 없거나 존재하지 않는 글입니다.")
    }

    private fun validAddPermission(ticker: String, sessionItem: SessionItem): Boolean =
        boardTickerRepository.findByIdOrNull(ticker)?.run {
            writePostRoles.isEmpty() || sessionItem.roles.any { it in writePostRoles }
        } ?: false
}
