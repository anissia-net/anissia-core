package anissia.dto

import anissia.rdb.domain.BoardPost
import java.time.LocalDateTime

data class BoardPostDto (
    var postNo: Long = 0,
    var topicNo: Long = 0,
    var content: String = "",
    var name: String = "",
    var regDt: LocalDateTime = LocalDateTime.now(),
    var updDt: LocalDateTime = LocalDateTime.now(),
) {
    constructor(boardPost: BoardPost): this(
        postNo = boardPost.postNo,
        topicNo = boardPost.topicNo,
        content = boardPost.content,
        name = boardPost.account?.name ?: "탈퇴회원",
        regDt = boardPost.regDt,
        updDt = boardPost.updDt,
    )
}