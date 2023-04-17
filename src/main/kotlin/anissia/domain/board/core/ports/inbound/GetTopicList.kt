package anissia.domain.board.core.ports.inbound

import anissia.domain.board.core.model.BoardTopicItem
import anissia.domain.board.core.model.GetTopicListCommand
import org.springframework.data.domain.Page

interface GetTopicList {
    fun handle(cmd: GetTopicListCommand): Page<BoardTopicItem>
}
