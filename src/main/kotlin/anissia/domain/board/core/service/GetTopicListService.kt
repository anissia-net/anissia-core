package anissia.domain.board.core.service

import anissia.domain.board.core.model.BoardTopicItem
import anissia.domain.board.core.model.GetTopicListCommand
import anissia.domain.board.core.ports.inbound.GetTopicList
import anissia.domain.board.core.ports.outbound.BoardTopicRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class GetTopicListService(
    private val boardTopicRepository: BoardTopicRepository,
): GetTopicList {
    override fun handle(cmd: GetTopicListCommand): Page<BoardTopicItem> =
        boardTopicRepository
            .findAllWithAccountByTickerOrderByTickerAscFixedDescTopicNoDesc(cmd.ticker, PageRequest.of(cmd.page, 20))
            .map { BoardTopicItem(it) }
}
