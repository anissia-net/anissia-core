package anissia.domain.board.service

import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.service.ActivePanelService
import anissia.domain.board.BoardPost
import anissia.domain.board.BoardTopic
import anissia.domain.board.command.DeletePostCommand
import anissia.domain.board.command.EditPostCommand
import anissia.domain.board.command.NewPostCommand
import anissia.domain.board.repository.BoardPostRepository
import anissia.domain.board.repository.BoardTickerRepository
import anissia.domain.board.repository.BoardTopicRepository
import anissia.domain.session.model.SessionItem
import anissia.infrastructure.common.subscribeBoundedElastic
import anissia.shared.ApiFailException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class PostServiceImpl(
    private val boardPostRepository: BoardPostRepository,
    private val boardTopicRepository: BoardTopicRepository,
    private val activePanelService: ActivePanelService,
    private val boardTickerRepository: BoardTickerRepository,
): PostService {

    @Transactional
    override fun add(cmd: NewPostCommand, sessionItem: SessionItem): Mono<String> =
        Mono.defer {
            cmd.validate()
            sessionItem.validateLogin()

            Mono.justOrEmpty<BoardTopic>(boardTopicRepository.findByIdOrNull(cmd.topicNo))
                .filter { validAddPermission(it.ticker, sessionItem) }
                .map {
                    boardPostRepository.save(BoardPost.create(topicNo = cmd.topicNo, content = cmd.content, an = sessionItem.an,))
                    boardTopicRepository.updatePostCount(cmd.topicNo)
                    ""
                }.switchIfEmpty(Mono.error(ApiFailException("권한이 없거나 존재하지 않는 글 혹은 게시판입니다.")))
        }

    @Transactional
    override fun edit(cmd: EditPostCommand, sessionItem: SessionItem): Mono<String> =
        Mono.defer {
            cmd.validate()
            sessionItem.validateLogin()

            Mono.justOrEmpty<BoardPost>(boardPostRepository.findByIdOrNull(cmd.postNo))
                .filter { !it.root && it.an == sessionItem.an }
                .map { boardPostRepository.save(it.apply { edit(cmd.content) }); "" }
                .switchIfEmpty(Mono.error(ApiFailException("권한이 없거나 존재하지 않는 글입니다.")))
        }

    @Transactional
    override fun delete(cmd: DeletePostCommand, sessionItem: SessionItem): Mono<String> =
        Mono.defer {
            cmd.validate()
            sessionItem.validateLogin()

            Mono.justOrEmpty<BoardPost>(boardPostRepository.findByIdOrNull(cmd.postNo))
                .filter { !it.root && (it.an == sessionItem.an || sessionItem.isAdmin) }
                .map {
                    if (it.an != sessionItem.an) {
                        activePanelService.add(ActivePanel(
                            published = false,
                            code = "DEL",
                            an = sessionItem.an,
                            data1 = "[${sessionItem.name}]님이 댓글을 삭제했습니다.",
                            data2 = "작성자/회원번호: ${it.account?.name}/${it.an}",
                            data3 = it.content,
                        )).subscribeBoundedElastic()
                    }
                    boardPostRepository.delete(it)
                    boardTopicRepository.updatePostCount(it.topicNo)
                    ""
                }.switchIfEmpty(Mono.error(ApiFailException("권한이 없거나 존재하지 않는 글입니다.")))
        }

    private fun validAddPermission(ticker: String, sessionItem: SessionItem): Boolean =
        boardTickerRepository.findByIdOrNull(ticker)?.run {
            writePostRoles.isEmpty() || sessionItem.roles.any { it in writePostRoles }
        } ?: false
}
