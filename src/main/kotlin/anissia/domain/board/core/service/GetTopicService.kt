package anissia.domain.board.core.service

import anissia.domain.board.core.model.BoardTopicItem
import anissia.domain.board.core.model.GetTopicCommand
import anissia.domain.board.core.ports.inbound.GetTopic
import anissia.domain.board.core.ports.outbound.BoardPostRepository
import anissia.domain.board.core.ports.outbound.BoardTopicRepository
import org.springframework.stereotype.Service

@Service
class GetTopicService(
    private val boardPostRepository: BoardPostRepository,
    private val boardTopicRepository: BoardTopicRepository,
): GetTopic {
    override fun handle(cmd: GetTopicCommand): BoardTopicItem =
        boardTopicRepository
            .findWithAccountByTickerAndTopicNo(cmd.ticker, cmd.topicNo)
            ?.let { BoardTopicItem(it, boardPostRepository.findAllWithAccountByTopicNoOrderByPostNo(it.topicNo)) }
            ?: BoardTopicItem()
}
