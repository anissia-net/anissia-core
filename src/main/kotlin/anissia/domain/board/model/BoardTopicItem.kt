package anissia.domain.board.model

import anissia.domain.board.BoardPost
import anissia.domain.board.BoardTopic
import com.fasterxml.jackson.annotation.JsonInclude

class BoardTopicItem (
    val topicNo: Long = 0,
    val fixed: Boolean = false,
    val topic: String = "",
    val postCount: Int = 0,
    val regTime: Long = 0L,
    val name: String = "",
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val posts: List<BoardPostItem>? = null
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
