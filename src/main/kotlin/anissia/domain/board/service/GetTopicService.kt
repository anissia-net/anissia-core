package anissia.domain.board.service

import anissia.domain.board.model.BoardTopicItem
import anissia.domain.board.command.GetTopicCommand
import anissia.domain.board.repository.BoardPostRepository
import anissia.domain.board.repository.BoardTopicRepository
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
