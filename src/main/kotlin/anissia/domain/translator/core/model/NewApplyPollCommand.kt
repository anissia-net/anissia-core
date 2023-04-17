package anissia.domain.translator.core.model

class NewApplyPollCommand(
    var applyNo: Long,
    var point: String = "",
    var comment: String = ""
) {
    fun validate() {
        require(applyNo > 0) { "잘못된 번호" }
        require(Regex("-1|0|1|").matches(point)) { "찬반의 코드 이상" }
        require(comment.isNotBlank()) { "의견을 입력해주세요." }
    }
}
