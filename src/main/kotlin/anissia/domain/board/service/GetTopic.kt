package anissia.domain.board.service

import anissia.domain.board.model.BoardTopicItem
import anissia.domain.board.command.GetTopicCommand

interface GetTopic {
    fun handle(cmd: GetTopicCommand): BoardTopicItem
}
