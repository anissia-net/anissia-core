package anissia.domain.board.service

import anissia.domain.activePanel.command.AddDeleteTopicLogActivePanelCommand
import anissia.domain.activePanel.service.ActivePanelService
import anissia.domain.board.BoardPost
import anissia.domain.board.BoardTopic
import anissia.domain.board.command.*
import anissia.domain.board.model.BoardTopicItem
import anissia.domain.board.repository.BoardPostRepository
import anissia.domain.board.repository.BoardTickerRepository
import anissia.domain.board.repository.BoardTopicRepository
import anissia.domain.session.model.SessionItem
import anissia.infrastructure.common.As.Companion.doOnNextMono
import anissia.shared.ApiFailException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TopicServiceImpl(
    private val boardTopicRepository: BoardTopicRepository,
    private val boardPostRepository: BoardPostRepository,
    private val boardTickerRepository: BoardTickerRepository,
    private val activePanelService: ActivePanelService,
): TopicService {

    override fun get(cmd: GetTopicCommand): Mono<BoardTopicItem> =
        boardTopicRepository.findWithAccountByTickerAndTopicNo(cmd.ticker, cmd.topicNo)
            .flatMap { topic ->
                boardPostRepository.findAllWithAccountByTopicNoOrderByPostNo(topic.topicNo).collectList()
                    .map { BoardTopicItem(topic, it) }
            }
            .switchIfEmpty(Mono.fromCallable { BoardTopicItem() })

    override fun getList(cmd: GetTopicListCommand): Mono<Page<BoardTopicItem>> =
        boardTopicRepository.findAllWithAccountByTickerOrderByTickerAscFixedDescTopicNoDesc(cmd.ticker, PageRequest.of(cmd.page, 20))
            .map { it.map(::BoardTopicItem) }

    override fun getMainRecent(): Mono<Map<String, List<Map<String, Any>>>> =
        Mono.zip(getRecent("notice").collectList(), getRecent("inquiry").collectList())
            .map { t -> mapOf("notice" to t.t1, "inquiry" to t.t2) }

    private fun getRecent(ticker: String): Flux<Map<String, Any>> =
        boardTopicRepository.findTop5ByTickerAndFixedOrderByTopicNoDesc(ticker)
            .map { mapOf(
                "topicNo" to it.topicNo,
                "topic" to it.topic,
                "postCount" to it.postCount,
                "regTime" to it.regDt.toEpochSecond()
            ) }

    @Transactional
    override fun add(cmd: NewTopicCommand, sessionItem: SessionItem): Mono<Long> =
        Mono.just(cmd.ticker)
            .doOnNext { sessionItem.validateLogin() }
            .doOnNext { cmd.validate() }
            .filterWhen { permission(it, sessionItem) }
            .switchIfEmpty(Mono.error(ApiFailException("권한이 없습니다.")))
            .flatMap { boardTopicRepository.save(BoardTopic.create(ticker = cmd.ticker, topic = cmd.topic, an = sessionItem.an)) }
            .flatMap { boardPostRepository.save(BoardPost.createRootPost(topicNo = it.topicNo, content = cmd.content, an = sessionItem.an)) }
            .map { it.topicNo }

    private fun permission(ticker: String, sessionItem: SessionItem): Mono<Boolean> =
        boardTickerRepository.findById(ticker)
            .map { boardTicker -> boardTicker.writeTopicRoles.isEmpty() || sessionItem.roles.any { it in boardTicker.writeTopicRoles } }

    @Transactional
    override fun edit(cmd: EditTopicCommand, sessionItem: SessionItem): Mono<Void> =
        Mono.just(cmd)
            .doOnNext { sessionItem.validateLogin() }
            .doOnNext { cmd.validate() }
            .flatMap { boardTopicRepository.findById(cmd.topicNo) }
            .filter { it.an == sessionItem.an }
            .flatMap { boardPostRepository.findWithAccountByTopicNoAndRootIsTrue(cmd.topicNo) }
            .switchIfEmpty(Mono.error(ApiFailException("권한이 없거나 존재하지 않는 글입니다.")))
            .flatMap { boardPostRepository.save(it.apply { edit(cmd.content) }) }
            .then()

    @Transactional
    override fun delete(cmd: DeleteTopicCommand, sessionItem: SessionItem): Mono<Void> =
        Mono.just(cmd)
            .doOnNext { sessionItem.validateLogin() }
            .doOnNext { cmd.validate() }
            .flatMap { boardTopicRepository.findById(cmd.topicNo) }
            .filter { it.an == sessionItem.an || sessionItem.isAdmin }
            .switchIfEmpty(Mono.error(ApiFailException("권한이 없거나 존재하지 않는 글입니다.")))
            .doOnNextMono { topic ->
                Mono.just(topic)
                    .filter { it.an != sessionItem.an }
                    .flatMap { boardPostRepository.findWithAccountByTopicNoAndRootIsTrue(it.topicNo) }
                    .flatMap { activePanelService.addDeleteTopic(AddDeleteTopicLogActivePanelCommand(topic, it, it.account), sessionItem) }
            }
            .flatMap { boardPostRepository.deleteAllByTopicNo(it.topicNo).thenReturn(it) }
            .flatMap { boardTopicRepository.delete(it) }
            .then()
}
