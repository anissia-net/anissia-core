package anissia.domain.board.core.model

import anissia.domain.board.core.BoardPost

class BoardPostItem (
    var postNo: Long = 0,
    var topicNo: Long = 0,
    var root: Boolean = false,
    var content: String = "",
    var name: String = "",
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
