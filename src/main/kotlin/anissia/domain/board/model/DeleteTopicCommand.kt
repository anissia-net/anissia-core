package anissia.domain.board.model

import gs.shared.FailException

class DeleteTopicCommand(
    var topicNo: Long,
) {
    fun validate() {
        if (topicNo <= 0)
            throw FailException()
    }
}

