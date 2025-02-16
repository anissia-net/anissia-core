package anissia.domain.board.command

import anissia.shared.ApiFailException

class EditPostCommand(
    var postNo: Long,
    val content: String = "",
) {
    fun validate() {
        if (postNo <= 0)
            throw ApiFailException()

        if (content.isBlank())
            throw ApiFailException("내용을 입력해 주세요.")
    }
}

