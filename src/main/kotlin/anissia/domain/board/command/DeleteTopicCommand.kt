package anissia.domain.board.command

import anissia.shared.ApiFailException

class DeleteTopicCommand(
    var topicNo: Long,
) {
    fun validate() {
        if (topicNo <= 0)
            throw ApiFailException()
    }
}

