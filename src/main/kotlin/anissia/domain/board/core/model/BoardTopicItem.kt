package anissia.domain.board.core.model

import anissia.domain.board.core.BoardPost
import anissia.domain.board.core.BoardTopic
import com.fasterxml.jackson.annotation.JsonInclude

class BoardTopicItem (
    var topicNo: Long = 0,
    var fixed: Boolean = false,
    var topic: String = "",
    var postCount: Int = 0,
    val regTime: Long = 0L,
    var name: String = "",
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var posts: List<BoardPostItem>? = null
) {
    constructor(boardTopic: BoardTopic, posts: List<BoardPost>? = null): this(
        topicNo = boardTopic.topicNo,
        fixed = boardTopic.fixed,
        topic = boardTopic.topic,
        postCount = boardTopic.postCount,
        regTime = boardTopic.regDt.toEpochSecond(),
        name = boardTopic.account?.name ?: "탈퇴회원",
        posts = posts ?.map { BoardPostItem(it) }
    )
}
