package anissia.domain.board.service

import anissia.domain.board.model.BoardTopicItem
import anissia.domain.board.command.GetTopicListCommand
import anissia.domain.board.repository.BoardTopicRepository
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
