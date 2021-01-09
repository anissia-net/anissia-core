package anissia.dto

import anissia.domain.BoardPost
import anissia.domain.BoardTopic
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

data class BoardTopicDto (
    var topicNo: Long = 0,
    var fixed: Boolean = false,
    var topic: String = "",
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var content: String = "",
    var postCount: Int = 0,
    var regDt: LocalDateTime = LocalDateTime.now(),
    var name: String = "",
    var posts: List<BoardPost> = listOf()
) {
    constructor(boardTopic: BoardTopic, includeContent: Boolean = false, posts: List<BoardPost> = listOf()): this(
        topicNo = boardTopic.topicNo,
        fixed = boardTopic.fixed,
        topic = boardTopic.topic,
        content = if (includeContent) boardTopic.content else "",
        postCount = boardTopic.postCount,
        regDt = boardTopic.regDt,
        name = boardTopic.account?.name ?: "탈퇴회원",
        posts = posts
    )
}