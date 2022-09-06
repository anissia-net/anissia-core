package anissia.dto

import anissia.rdb.entity.BoardPost
import anissia.rdb.entity.BoardTopic
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

data class BoardTopicDto (
    var topicNo: Long = 0,
    var fixed: Boolean = false,
    var topic: String = "",
    var postCount: Int = 0,
    var regDt: LocalDateTime = LocalDateTime.now(),
    var name: String = "",
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var posts: List<BoardPostDto>? = null
) {
    constructor(boardTopic: BoardTopic, posts: List<BoardPost>? = null): this(
        topicNo = boardTopic.topicNo,
        fixed = boardTopic.fixed,
        topic = boardTopic.topic,
        postCount = boardTopic.postCount,
        regDt = boardTopic.regDt,
        name = boardTopic.account?.name ?: "탈퇴회원",
        posts = posts ?.map { BoardPostDto(it) }
    )
}