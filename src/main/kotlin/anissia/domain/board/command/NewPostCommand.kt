package anissia.domain.board.command

import anissia.shared.ApiFailException

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

