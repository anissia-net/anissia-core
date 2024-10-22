package anissia.domain.board.model

import gs.shared.FailException

class NewPostCommand(
    var topicNo: Long,
    val content: String = "",
) {
    fun validate() {
        if (topicNo <= 0)
            throw FailException()

        if (content.isBlank())
            throw FailException("내용을 입력해 주세요.")
    }
}

