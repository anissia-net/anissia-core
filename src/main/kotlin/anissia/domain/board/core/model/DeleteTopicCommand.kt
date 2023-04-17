package anissia.domain.board.core.model

import gs.shared.FailException

class DeleteTopicCommand(
    var topicNo: Long,
) {
    fun validate() {
        if (topicNo <= 0)
            throw FailException()
    }
}

