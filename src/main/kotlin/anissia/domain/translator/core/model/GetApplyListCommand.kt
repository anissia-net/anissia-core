package anissia.domain.translator.core.model

class GetApplyListCommand(
    val page: Int
) {
    fun validate() {
        require(page >= 0) { "잘못된 페이지" }
    }
}
