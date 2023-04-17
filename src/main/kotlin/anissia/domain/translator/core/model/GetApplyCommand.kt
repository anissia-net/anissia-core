package anissia.domain.translator.core.model

class GetApplyCommand(
    val applyNo: Long
) {
    fun validate() {
        require(applyNo > 0) { "잘못된 번호" }
    }
}
