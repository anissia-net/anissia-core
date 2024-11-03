package anissia.domain.board.command

import gs.shared.FailException

class NewTopicCommand(
    var ticker: String = "",
    var topic: String = "",
    var content: String = ""
) {
    fun validate() {
        if (ticker.isBlank())
            throw FailException()

        if (topic.isBlank())
            throw FailException("제목을 입력해 주세요.")

        if (content.isBlank())
            throw FailException("내용을 입력해 주세요.")
    }
}

