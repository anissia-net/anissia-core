package anissia.domain.board.model

import gs.shared.FailException

class DeletePostCommand(
    var postNo: Long,
) {
    fun validate() {
        if (postNo <= 0)
            throw FailException()
    }
}
