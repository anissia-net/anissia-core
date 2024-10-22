package anissia.domain.board.service

import anissia.domain.board.model.BoardTopicItem
import anissia.domain.board.model.GetTopicListCommand
import org.springframework.data.domain.Page

interface GetTopicList {
    fun handle(cmd: GetTopicListCommand): Page<BoardTopicItem>
}
