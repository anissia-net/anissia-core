package anissia.domain.board.command

import anissia.shared.ApiFailException

class EditTopicCommand(
    var topicNo: Long,
    val topic: String = "",
    val content: String = "",
) {
    fun validate() {
        if (topicNo <= 0)
            throw ApiFailException()

        if (content.isBlank())
            throw ApiFailException("내용을 입력해 주세요.")
    }
}

