package anissia.domain.board.command

import gs.shared.FailException

class DeletePostCommand(
    var postNo: Long,
) {
    fun validate() {
        if (postNo <= 0)
            throw FailException()
    }
}
