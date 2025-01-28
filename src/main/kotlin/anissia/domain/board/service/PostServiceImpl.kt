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
import anissia.shared.ApiFailException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class PostServiceImpl(
    private val boardPostRepository: BoardPostRepository,
    private val boardTopicRepository: BoardTopicRepository,
    private val activePanelRepository: ActivePanelRepository,
    private val boardTickerRepository: BoardTickerRepository,
): PostService {

    @Transactional
    override fun add(cmd: NewPostCommand, sessionItem: SessionItem): Mono<Void> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .doOnNext { sessionItem.validateLogin() }
            .flatMap { boardTopicRepository.findById(it.topicNo) }
            .filterWhen { validAddPermission(it.ticker, sessionItem) }
            .switchIfEmpty(Mono.error(ApiFailException("권한이 없거나 존재하지 않는 게시판입니다.")))
            .flatMap { boardPostRepository.save(BoardPost.create(topicNo = cmd.topicNo, content = cmd.content, an = sessionItem.an)) }
            .flatMap { boardTopicRepository.updatePostCount(cmd.topicNo) }
            .then()

    @Transactional
    override fun edit(cmd: EditPostCommand, sessionItem: SessionItem): Mono<Void> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .doOnNext { sessionItem.validateLogin() }
            .flatMap { boardPostRepository.findById(it.postNo) }
            .filter { !it.root && it.an == sessionItem.an }
            .switchIfEmpty(Mono.error(ApiFailException("권한이 없거나 존재하지 않는 글입니다.")))
            .flatMap { boardPostRepository.save(it.apply { edit(cmd.content) }) }
            .then()

    @Transactional
    override fun delete(cmd: DeletePostCommand, sessionItem: SessionItem): Mono<Void> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .doOnNext { sessionItem.validateLogin() }
            .flatMap { boardPostRepository.findById(cmd.postNo) }
            .filter { !it.root && (it.an == sessionItem.an || sessionItem.isAdmin) }
            .switchIfEmpty(Mono.error(ApiFailException("권한이 없거나 존재하지 않는 글입니다.")))
            .flatMap { post ->
                Mono.just(post)
                    .filter { it.an != sessionItem.an }
                    .flatMap {
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
                    .then(Mono.fromCallable { post })
            }
            .flatMap { post -> boardPostRepository.delete(post).then(Mono.fromCallable { post.topicNo }) }
            .flatMap { boardTopicRepository.updatePostCount(it) }
            .then()

    private fun validAddPermission(ticker: String, sessionItem: SessionItem): Mono<Boolean> =
        boardTickerRepository.findById(ticker)
            .map { board -> board.writePostRoles.isEmpty() || sessionItem.roles.any { it in board.writePostRoles } }
}
