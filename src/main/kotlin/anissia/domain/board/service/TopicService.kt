package anissia.domain.board.service

import anissia.domain.board.command.*
import anissia.domain.board.model.BoardTopicItem
import anissia.domain.session.model.SessionItem
import org.springframework.data.domain.Page
import reactor.core.publisher.Mono

interface TopicService {
    fun get(cmd: GetTopicCommand): Mono<BoardTopicItem>
    fun getList(cmd: GetTopicListCommand): Mono<Page<BoardTopicItem>>
    fun getMainRecent(): Mono<Map<String, List<Map<String, Any>>>>
    fun add(cmd: NewTopicCommand, sessionItem: SessionItem): Mono<Long>
    fun edit(cmd: EditTopicCommand, sessionItem: SessionItem): Mono<String>
    fun delete(cmd: DeleteTopicCommand, sessionItem: SessionItem): Mono<String>
}
