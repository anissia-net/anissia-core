package anissia.domain.board.service

import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.repository.ActivePanelRepository
import anissia.domain.board.BoardPost
import anissia.domain.board.BoardTopic
import anissia.domain.board.command.*
import anissia.domain.board.model.BoardTopicItem
import anissia.domain.board.repository.BoardPostRepository
import anissia.domain.board.repository.BoardTickerRepository
import anissia.domain.board.repository.BoardTopicRepository
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TopicServiceImpl(
    private val boardTopicRepository: BoardTopicRepository,
    private val boardPostRepository: BoardPostRepository,
    private val boardTickerRepository: BoardTickerRepository,
    private val activePanelRepository: ActivePanelRepository,
): TopicService {
    override fun get(cmd: GetTopicCommand): BoardTopicItem =
        boardTopicRepository
            .findWithAccountByTickerAndTopicNo(cmd.ticker, cmd.topicNo)
            ?.let { BoardTopicItem(it, boardPostRepository.findAllWithAccountByTopicNoOrderByPostNo(it.topicNo)) }
            ?: BoardTopicItem()

    override fun getList(cmd: GetTopicListCommand): Page<BoardTopicItem> =
        boardTopicRepository
            .findAllWithAccountByTickerOrderByTickerAscFixedDescTopicNoDesc(cmd.ticker, PageRequest.of(cmd.page, 20))
            .map { BoardTopicItem(it) }

    override fun getMainRecent(): Map<String, List<Map<String, Any>>> =
        mapOf("notice" to getRecent("notice"), "inquiry" to getRecent("inquiry"))

    private fun getRecent(ticker: String): List<Map<String, Any>> =
        boardTopicRepository
            .findTop5ByTickerAndFixedOrderByTopicNoDesc(ticker)
            .map { mapOf(
                "topicNo" to it.topicNo,
                "topic" to it.topic,
                "postCount" to it.postCount,
                "regTime" to it.regDt.toEpochSecond()
            ) }

    @Transactional
    override fun add(cmd: NewTopicCommand, session: Session): ResultWrapper<Long> {
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

    @Transactional
    override fun edit(cmd: EditTopicCommand, session: Session): ResultWrapper<Unit> {
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

    @Transactional
    override fun delete(cmd: DeleteTopicCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()
        session.validateLogin()

        return boardTopicRepository
            .findByIdOrNull(cmd.topicNo)
            ?.takeIf { it.an == session.an || session.isAdmin }
            ?.let {
                if (it.an != session.an) {
                    activePanelRepository.save(
                        ActivePanel(
                            published = false,
                            code = "DEL",
                            an = session.an,
                            data1 = "[${session.name}]님이 글을 삭제했습니다.",
                            data2 = "작성자/회원번호: ${it.account?.name}/${it.an}",
                            data3 = "${it.topic}\n${boardPostRepository.findWithAccountByTopicNoAndRootIsTrue(it.topicNo)?.content}",
                        )
                    )
                }
                boardPostRepository.deleteAllByTopicNo(cmd.topicNo)
                boardTopicRepository.delete(it)
                ResultWrapper.ok()
            }
            ?: ResultWrapper.fail("권한이 없거나 존재하지 않는 글입니다.")
    }
}
