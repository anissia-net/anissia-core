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

@Service
class BoardService(
    private val boardTopicRepository: BoardTopicRepository,
    private val boardPostRepository: BoardPostRepository,
    private val boardTickerRepository: BoardTickerRepository
) {
    private val tickerCacheStore = CacheStore<String, String>((24 * 60 * 60000).toLong())

    fun getTicker(ticker: String): String = tickerCacheStore.get(ticker) { As.toJsonString(boardTickerRepository.findById(ticker)) }

    fun getList(ticker: String, page: Int): Page<BoardTopicDto> =
        boardTopicRepository
            .findAllWithAccountByTickerOrderByTickerAscFixedDescTopicNoDesc(ticker, PageRequest.of(page, 20))
            .map { BoardTopicDto(it) }
}