package anissia.services

import anissia.dto.BoardTopicDto
import anissia.misc.As
import anissia.repository.BoardPostRepository
import anissia.repository.BoardTopicRepository
import anissia.repository.BoardTickerRepository
import me.saro.kit.CacheStore
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service
class BoardService(
    private val boardTopicRepository: BoardTopicRepository,
    private val boardPostRepository: BoardPostRepository,
    private val boardTickerRepository: BoardTickerRepository
) {
    private val tickerCacheStore = CacheStore<String, String>((24 * 60 * 60000).toLong())
    private val recentCacheStore = CacheStore<Int, String>((5 * 60000).toLong())

    fun getTicker(ticker: String): String = tickerCacheStore.get(ticker) { boardTickerRepository.findById(ticker).map { As.toJsonString(it) }.orElse("{}") }

    fun getTopic(ticker: String, topicNo: Long) = boardTopicRepository.findWithAccountByTickerAndTopicNo(ticker, topicNo)

    fun getList(ticker: String, page: Int): Page<BoardTopicDto> =
        boardTopicRepository
            .findAllWithAccountByTickerOrderByTickerAscFixedDescTopicNoDesc(ticker, PageRequest.of(page, 20))
            .map { BoardTopicDto(it) }

    fun getRecent(): String =
        recentCacheStore.get(1) { As.toJsonString(mapOf("notice" to getRecent("notice"), "inquiry" to getRecent("inquiry"))) }

    private fun getRecent(ticker: String) =
        boardTopicRepository
            .findTop5ByTickerAndFixedOrderByTopicNoDesc(ticker)
            .map { mapOf(
                "topicNo" to it.topicNo,
                "title" to "${it.topic}${if (it.postCount > 0) " (${it.postCount})" else ""}",
                "regDt" to it.regDt.format(DateTimeFormatter.ISO_DATE_TIME)
            ) }
}