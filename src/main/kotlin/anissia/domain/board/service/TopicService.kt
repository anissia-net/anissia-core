package anissia.domain.board.service

import anissia.domain.board.command.*
import anissia.domain.board.model.BoardTopicItem
import anissia.domain.session.model.SessionItem
import anissia.shared.ApiResponse
import org.springframework.data.domain.Page

interface TopicService {
    fun get(cmd: GetTopicCommand): BoardTopicItem
    fun getList(cmd: GetTopicListCommand): Page<BoardTopicItem>
    fun getMainRecent(): Map<String, List<Map<String, Any>>>
    fun add(cmd: NewTopicCommand, sessionItem: SessionItem): ApiResponse<Long>
    fun edit(cmd: EditTopicCommand, sessionItem: SessionItem): ApiResponse<Unit>
    fun delete(cmd: DeleteTopicCommand, sessionItem: SessionItem): ApiResponse<Unit>
}
