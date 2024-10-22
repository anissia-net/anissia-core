package anissia.domain.board.service

import anissia.domain.board.repository.BoardTopicRepository
import org.springframework.stereotype.Service

@Service
class GetTopicRecentForHomeService(
    private val boardTopicRepository: BoardTopicRepository,
): GetTopicRecentForHome {
    override fun handle(): Map<String, List<Map<String, Any>>> =
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

}
