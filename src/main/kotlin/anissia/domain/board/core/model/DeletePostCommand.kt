package anissia.domain.board.core.model

import gs.shared.FailException

class DeletePostCommand(
    var postNo: Long,
) {
    fun validate() {
        if (postNo <= 0)
            throw FailException()
    }
}
