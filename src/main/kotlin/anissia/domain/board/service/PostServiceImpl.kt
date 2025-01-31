package anissia.domain.board.service

import anissia.domain.activePanel.command.AddDeletePostLogActivePanelCommand
import anissia.domain.activePanel.service.ActivePanelService
import anissia.domain.board.BoardPost
import anissia.domain.board.command.DeletePostCommand
import anissia.domain.board.command.EditPostCommand
import anissia.domain.board.command.NewPostCommand
import anissia.domain.board.repository.BoardPostRepository
import anissia.domain.board.repository.BoardTickerRepository
import anissia.domain.board.repository.BoardTopicRepository
import anissia.domain.session.model.SessionItem
import anissia.infrastructure.common.doOnNextMono
import anissia.shared.ApiFailException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class PostServiceImpl(
    private val boardPostRepository: BoardPostRepository,
    private val boardTopicRepository: BoardTopicRepository,
    private val boardTickerRepository: BoardTickerRepository,
    private val activePanelService: ActivePanelService,
): PostService {

    @Transactional
    override fun add(cmd: NewPostCommand, sessionItem: SessionItem): Mono<String> =
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
    override fun edit(cmd: EditPostCommand, sessionItem: SessionItem): Mono<String> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .doOnNext { sessionItem.validateLogin() }
            .flatMap { boardPostRepository.findById(it.postNo) }
            .filter { it.an == sessionItem.an }
            .switchIfEmpty(Mono.error(ApiFailException("권한이 없거나 존재하지 않는 글입니다.")))
            .flatMap { boardPostRepository.save(it.apply { edit(cmd.content) }) }
            .then()

    @Transactional
    override fun delete(cmd: DeletePostCommand, sessionItem: SessionItem): Mono<String> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .doOnNext { sessionItem.validateLogin() }
            .flatMap { boardPostRepository.findById(cmd.postNo) }
            .filter { it.an == sessionItem.an || sessionItem.isAdmin }
            .switchIfEmpty(Mono.error(ApiFailException("권한이 없거나 존재하지 않는 글입니다.")))
            .doOnNextMono { post ->
                Mono.just(post)
                    .filter { it.an != sessionItem.an }
                    .flatMap { activePanelService.addDeletePost(AddDeletePostLogActivePanelCommand(post, post.account), sessionItem) }
            }
            .doOnNextMono { boardPostRepository.delete(it) }
            .flatMap { boardTopicRepository.updatePostCount(it.topicNo) }
            .then()

    private fun validAddPermission(ticker: String, sessionItem: SessionItem): Mono<Boolean> =
        boardTickerRepository.findById(ticker)
            .map { board -> board.writePostRoles.isEmpty() || sessionItem.roles.any { it in board.writePostRoles } }
}
