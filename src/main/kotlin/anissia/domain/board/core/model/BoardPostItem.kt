package anissia.domain.board.core.model

import anissia.domain.board.core.BoardPost

class BoardPostItem (
    val postNo: Long = 0,
    val topicNo: Long = 0,
    val root: Boolean = false,
    val content: String = "",
    val name: String = "",
    val regTime: Long = 0L,
    val updTime: Long = 0L,
) {
    constructor(boardPost: BoardPost): this(
        postNo = boardPost.postNo,
        topicNo = boardPost.topicNo,
        root = boardPost.root,
        content = boardPost.content,
        name = boardPost.account?.name ?: "탈퇴회원",
        regTime = boardPost.regDt.toEpochSecond(),
        updTime = boardPost.updDt.toEpochSecond(),
    )
}
