package anissia.domain.board.command

import anissia.shared.ApiFailException

class DeletePostCommand(
    var postNo: Long,
) {
    fun validate() {
        if (postNo <= 0)
            throw ApiFailException()
    }
}
