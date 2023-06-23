package anissia.domain.board.core.model

import anissia.domain.board.core.BoardPost
import anissia.domain.board.core.BoardTopic
import com.fasterxml.jackson.annotation.JsonInclude

class BoardTopicItem (
    val topicNo: Long,
    val fixed: Boolean,
    val topic: String,
    val postCount: Int,
    val regTime: Long,
    val name: String,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val posts: List<BoardPostItem>?,
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
