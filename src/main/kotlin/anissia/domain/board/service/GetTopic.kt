package anissia.domain.board.service

import anissia.domain.board.core.model.BoardTopicItem
import anissia.domain.board.core.model.GetTopicCommand

interface GetTopic {
    fun handle(cmd: GetTopicCommand): BoardTopicItem
}
