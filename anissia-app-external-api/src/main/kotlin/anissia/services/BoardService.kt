package anissia.services

import anissia.dto.BoardTopicDto
import anissia.misc.As
import anissia.dto.BoardTickerDto
import anissia.dto.ResultData
import anissia.dto.ResultStatus
import anissia.dto.request.BoardPostRequest
import anissia.dto.request.BoardTopicRequest
import anissia.rdb.domain.BoardPost
import anissia.rdb.domain.BoardTopic
import anissia.rdb.repository.BoardPostRepository
import anissia.rdb.repository.BoardTopicRepository
import anissia.rdb.repository.BoardTickerRepository
import me.saro.kit.CacheStore
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter
import javax.persistence.Column
import javax.persistence.Lob

@Service
class BoardService(
    private val boardTopicRepository: BoardTopicRepository,
    private val boardPostRepository: BoardPostRepository,
    private val boardTickerRepository: BoardTickerRepository,
    private val sessionService: SessionService
) {
    private val tickerCacheStore = CacheStore<String, String>((24 * 60 * 60000).toLong())
    private val recentCacheStore = CacheStore<Int, String>((5 * 60000).toLong())
    private val session get() = sessionService.session

    fun getTopic(ticker: String, topicNo: Long): BoardTopicDto =
        boardTopicRepository
            .findWithAccountByTickerAndTopicNo(ticker, topicNo)
            ?.let { BoardTopicDto(it, boardPostRepository.findAllWithAccountByTopicNoOrderByPostNo(topicNo)) }
            ?: BoardTopicDto()

    fun getList(ticker: String, page: Int): Page<BoardTopicDto> =
        boardTopicRepository
            .findAllWithAccountByTickerOrderByTickerAscFixedDescTopicNoDesc(ticker, PageRequest.of(page, 20))
            .map { BoardTopicDto(it) }

    @Transactional
    fun createTopic(ticker: String, boardTopicRequest: BoardTopicRequest) =
        ticker
            .takeIf { permission(it, "post") }
            ?.let {
                val topic = boardTopicRepository.saveAndFlush(BoardTopic(
                    ticker = ticker,
                    topic = boardTopicRequest.topic,
                    an = session!!.an,
                ))
                boardPostRepository.saveAndFlush(BoardPost(
                    topicNo = topic.topicNo,
                    root = true,
                    content = boardTopicRequest.content,
                    an = session!!.an,
                ))
                ResultData<Long>("OK", "", topic.topicNo)
            }
            ?: ResultData<Long>("FAIL", "권한이 없습니다.")

    @Transactional
    fun updateTopic(topicNo: Long, boardTopicRequest: BoardTopicRequest) =
        boardTopicRepository
            .findByIdOrNull(topicNo)
            ?.takeIf { it.an == session?.an || session?.isRoot() == true /* temp - delete */ }
            ?.let { node ->
                boardPostRepository
                    .findWithAccountByTopicNoAndRootIsTrue(topicNo)
                    ?.also { boardPostRepository.save(it.apply { content = boardTopicRequest.content }) }
                    ?: ResultStatus("FAIL", "권한이 없거나 존재하지 않는 글입니다.")
                boardTopicRepository.save(node.apply { topic = boardTopicRequest.topic })
                ResultStatus("OK")
            }
            ?: ResultStatus("FAIL", "권한이 없거나 존재하지 않는 글입니다.")

    @Transactional
    fun deleteTopic(topicNo: Long) =
        boardTopicRepository
            .findByIdOrNull(topicNo)
            ?.takeIf { it.an == session?.an || session?.isRoot() == true }
            ?.let {
                boardPostRepository.deleteAllByTopicNo(topicNo)
                boardTopicRepository.delete(it)
                ResultStatus("OK")
            }
            ?: ResultStatus("FAIL", "권한이 없거나 존재하지 않는 글입니다.")

    @Transactional
    fun createPost(topicNo: Long, boardPostRequest: BoardPostRequest) =
        boardTopicRepository
            .findByIdOrNull(topicNo)
            ?.takeIf { permission(it.ticker, "post") }
            ?.let {
                boardPostRepository.saveAndFlush(BoardPost(
                    topicNo = topicNo,
                    content = boardPostRequest.content,
                    an = session!!.an,
                )).run { ResultStatus("OK") }
            }
            ?: ResultStatus("FAIL", "권한이 없거나 존재하지 않는 글 혹은 게시판입니다.")

    @Transactional
    fun updatePost(postNo: Long, boardPostRequest: BoardPostRequest) =
        boardPostRepository
            .findByIdOrNull(postNo)
            ?.takeIf { !it.root && it.an == session?.an }
            ?.let {
                it.content = boardPostRequest.content
                boardPostRepository.save(it)
                ResultStatus("OK")
            }
            ?: ResultStatus("FAIL", "권한이 없거나 존재하지 않는 글입니다.")

    @Transactional
    fun deletePost(postNo: Long) =
        boardPostRepository
            .findByIdOrNull(postNo)
            ?.takeIf { !it.root && (it.an == session?.an || session?.isRoot() == true) }
            ?.let {
                boardPostRepository.delete(it)
                boardTopicRepository.updatePostCount(it.topicNo)
                ResultStatus("OK")
            }
            ?: ResultStatus("FAIL", "권한이 없거나 존재하지 않는 글입니다.")

    fun getRecent(): String =
        recentCacheStore.find(1) { As.toJsonString(mapOf("notice" to getRecent("notice"), "inquiry" to getRecent("inquiry"))) }

    fun getTickerCached(ticker: String): String =
        tickerCacheStore.find(ticker) { getTicker(ticker)?.let { e -> As.toJsonString(e) } ?: "{}" }

    private fun getTicker(ticker: String): BoardTickerDto? =
        boardTickerRepository.findByIdOrNull(ticker)?.let { BoardTickerDto(it) }

    private fun permission(ticker: String, type: String): Boolean =
        boardTickerRepository.findByIdOrNull(ticker)?.run {
            when (type) {
                "topic" -> writeTopicRoles.isEmpty() || session?.roles?.any { it in writeTopicRoles } == true
                "post" -> writePostRoles.isEmpty() || session?.roles?.any { it in writePostRoles } == true
                else -> false
            }
        } ?: false

    private fun getRecent(ticker: String): List<Map<String, Any>> =
        boardTopicRepository
            .findTop5ByTickerAndFixedOrderByTopicNoDesc(ticker)
            .map { mapOf(
                "topicNo" to it.topicNo,
                "title" to "${it.topic}${if (it.postCount > 0) " (${it.postCount})" else ""}",
                "regDt" to it.regDt.format(DateTimeFormatter.ISO_DATE_TIME)
            ) }
}