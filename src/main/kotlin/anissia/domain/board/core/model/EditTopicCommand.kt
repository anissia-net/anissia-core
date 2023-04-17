package anissia.domain.board.core.model

import gs.shared.FailException

class EditTopicCommand(
    var topicNo: Long,
    val topic: String = "",
    val content: String = "",
) {
    fun validate() {
        if (topicNo <= 0)
            throw FailException()

        if (content.isBlank())
            throw FailException("내용을 입력해 주세요.")
    }
}

