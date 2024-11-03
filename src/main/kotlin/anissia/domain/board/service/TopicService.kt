package anissia.domain.board.service

import anissia.domain.board.command.*
import anissia.domain.board.model.BoardTopicItem
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page
import org.springframework.transaction.annotation.Transactional

interface TopicService {
    fun get(cmd: GetTopicCommand): BoardTopicItem
    fun getList(cmd: GetTopicListCommand): Page<BoardTopicItem>
    fun getMainRecent(): Map<String, List<Map<String, Any>>>
    fun add(cmd: NewTopicCommand, session: Session): ResultWrapper<Long>
    fun edit(cmd: EditTopicCommand, session: Session): ResultWrapper<Unit>
    fun delete(cmd: DeleteTopicCommand, session: Session): ResultWrapper<Unit>
}
