package anissia.domain.board.command

import anissia.shared.ApiFailException

class NewTopicCommand(
    var ticker: String = "",
    var topic: String = "",
    var content: String = ""
) {
    fun validate() {
        if (ticker.isBlank())
            throw ApiFailException()

        if (topic.isBlank())
            throw ApiFailException("제목을 입력해 주세요.")

        if (content.isBlank())
            throw ApiFailException("내용을 입력해 주세요.")
    }
}

