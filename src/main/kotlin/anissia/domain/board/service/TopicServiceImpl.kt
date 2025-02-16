package anissia.domain.board.service

import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.service.ActivePanelService
import anissia.domain.board.BoardPost
import anissia.domain.board.BoardTopic
import anissia.domain.board.command.*
import anissia.domain.board.model.BoardTopicItem
import anissia.domain.board.repository.BoardPostRepository
import anissia.domain.board.repository.BoardTickerRepository
import anissia.domain.board.repository.BoardTopicRepository
import anissia.domain.session.model.SessionItem
import anissia.infrastructure.common.mapPageItem
import anissia.infrastructure.common.subscribeBoundedElastic
import anissia.shared.ApiFailException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class TopicServiceImpl(
    private val boardTopicRepository: BoardTopicRepository,
    private val boardPostRepository: BoardPostRepository,
    private val boardTickerRepository: BoardTickerRepository,
    private val activePanelService: ActivePanelService,
): TopicService {

    override fun get(cmd: GetTopicCommand): Mono<BoardTopicItem> =
        Mono.justOrEmpty<BoardTopic>(boardTopicRepository.findWithAccountByTickerAndTopicNo(cmd.ticker, cmd.topicNo))
            .map { BoardTopicItem(it, boardPostRepository.findAllWithAccountByTopicNoOrderByPostNo(it.topicNo)) }
            .switchIfEmpty(Mono.just(BoardTopicItem()))

    override fun getList(cmd: GetTopicListCommand): Mono<Page<BoardTopicItem>> =
        Mono.just(boardTopicRepository.findAllWithAccountByTickerOrderByTickerAscFixedDescTopicNoDesc(cmd.ticker, PageRequest.of(cmd.page, 20)))
            .mapPageItem { BoardTopicItem(it) }

    override fun getMainRecent(): Mono<Map<String, List<Map<String, Any>>>> =
        Mono.just(mapOf("notice" to getRecent("notice"), "inquiry" to getRecent("inquiry")))

    private fun getRecent(ticker: String): List<Map<String, Any>> =
        boardTopicRepository.findTop5ByTickerAndFixedOrderByTopicNoDesc(ticker)
            .map { mapOf(
                "topicNo" to it.topicNo,
                "topic" to it.topic,
                "postCount" to it.postCount,
                "regTime" to it.regDt.toEpochSecond()
            ) }

    @Transactional
    override fun add(cmd: NewTopicCommand, sessionItem: SessionItem): Mono<Long> =
        Mono.defer {
            if (!sessionItem.isLogin) {
                return@defer Mono.error(ApiFailException("로그인이 필요합니다.", 0))
            }

            Mono.just(cmd.ticker)
                .filter { permission(it, sessionItem) }
                .map {
                    val topic = boardTopicRepository.save(
                        BoardTopic.create(
                            ticker = cmd.ticker,
                            topic = cmd.topic,
                            an = sessionItem.an,
                        )
                    )
                    boardPostRepository.save(
                        BoardPost.createRootPost(
                            topicNo = topic.topicNo,
                            content = cmd.content,
                            an = sessionItem.an,
                        )
                    )
                    topic.topicNo
                }
                .switchIfEmpty(Mono.error(ApiFailException("권한이 없습니다.", -1)))
        }

    @Transactional
    override fun edit(cmd: EditTopicCommand, sessionItem: SessionItem): Mono<String> =
        Mono.defer {
            cmd.validate()
            sessionItem.validateLogin()

            Mono.justOrEmpty<BoardTopic>(boardTopicRepository.findByIdOrNull(cmd.topicNo))
                .filter { it.an == sessionItem.an }
                .flatMap { topic ->
                    Mono.justOrEmpty<BoardPost>(boardPostRepository.findWithAccountByTopicNoAndRootIsTrue(cmd.topicNo))
                        .map {
                            boardPostRepository.save(it.apply { edit(cmd.content) })
                            boardTopicRepository.save(topic.apply { edit(cmd.topic) })
                            ""
                        }.switchIfEmpty(Mono.error(ApiFailException("권한이 없거나 존재하지 않는 글입니다.", null)))
                }
        }

    @Transactional
    override fun delete(cmd: DeleteTopicCommand, sessionItem: SessionItem): Mono<String> =
        Mono.defer {
            cmd.validate()
            sessionItem.validateLogin()

            Mono.justOrEmpty<BoardTopic>(boardTopicRepository.findByIdOrNull(cmd.topicNo))
                .filter { it.an == sessionItem.an || sessionItem.isAdmin }
                .map {
                    if (it.an != sessionItem.an) {
                        activePanelService.add(
                            ActivePanel(
                                published = false,
                                code = "DEL",
                                an = sessionItem.an,
                                data1 = "[${sessionItem.name}]님이 글을 삭제했습니다.",
                                data2 = "작성자/회원번호: ${it.account?.name}/${it.an}",
                                data3 = "${it.topic}\n${boardPostRepository.findWithAccountByTopicNoAndRootIsTrue(it.topicNo)?.content}",
                            )
                        ).subscribeBoundedElastic()
                    }
                    boardPostRepository.deleteAllByTopicNo(cmd.topicNo)
                    boardTopicRepository.delete(it)
                    ""
                }.switchIfEmpty(Mono.error(ApiFailException("권한이 없거나 존재하지 않는 글입니다.")))
        }

    private fun permission(ticker: String, sessionItem: SessionItem): Boolean =
        boardTickerRepository.findByIdOrNull(ticker)?.run {
            writeTopicRoles.isEmpty() || sessionItem.roles.any { it in writeTopicRoles }
        } ?: false
}
