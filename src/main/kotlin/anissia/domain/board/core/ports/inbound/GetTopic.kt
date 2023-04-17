package anissia.domain.board.core.ports.inbound

import anissia.domain.board.core.model.BoardTopicItem
import anissia.domain.board.core.model.GetTopicCommand

interface GetTopic {
    fun handle(cmd: GetTopicCommand): BoardTopicItem
}
