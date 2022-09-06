package anissia.dto

import anissia.rdb.entity.BoardPost
import java.time.LocalDateTime

data class BoardPostDto (
    var postNo: Long = 0,
    var topicNo: Long = 0,
    var root: Boolean = false,
    var content: String = "",
    var name: String = "",
    var regDt: LocalDateTime = LocalDateTime.now(),
    var updDt: LocalDateTime = LocalDateTime.now(),
) {
    constructor(boardPost: BoardPost): this(
        postNo = boardPost.postNo,
        topicNo = boardPost.topicNo,
        root = boardPost.root,
        content = boardPost.content,
        name = boardPost.account?.name ?: "탈퇴회원",
        regDt = boardPost.regDt,
        updDt = boardPost.updDt,
    )
}