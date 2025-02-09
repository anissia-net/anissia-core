package anissia.domain.board.service

import anissia.domain.board.command.*
import anissia.domain.board.model.BoardTopicItem
import anissia.domain.session.model.SessionItem
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page

interface TopicService {
    fun get(cmd: GetTopicCommand): BoardTopicItem
    fun getList(cmd: GetTopicListCommand): Page<BoardTopicItem>
    fun getMainRecent(): Map<String, List<Map<String, Any>>>
    fun add(cmd: NewTopicCommand, sessionItem: SessionItem): Mono<ApiResponse<Long>
    fun edit(cmd: EditTopicCommand, sessionItem: SessionItem): Mono<String>
    fun delete(cmd: DeleteTopicCommand, sessionItem: SessionItem): Mono<String>
}
