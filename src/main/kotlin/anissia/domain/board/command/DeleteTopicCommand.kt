package anissia.domain.board.command

import gs.shared.FailException

class DeleteTopicCommand(
    var topicNo: Long,
) {
    fun validate() {
        if (topicNo <= 0)
            throw FailException()
    }
}

