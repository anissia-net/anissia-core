package anissia.domain.board.command

import gs.shared.FailException

class NewPostCommand(
    var topicNo: Long,
    val content: String = "",
) {
    fun validate() {
        if (topicNo <= 0)
            throw ApiFailException()

        if (content.isBlank())
            throw ApiFailException("내용을 입력해 주세요.")
    }
}

