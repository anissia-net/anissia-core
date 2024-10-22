package anissia.domain.activePanel.model

class NewActivePanelNoticeCommand(
    var query: String = ""
) {
    val published get() = query.startsWith("!")
    val commend get() = query.startsWith("/")

    val text get() = when {
        published -> query.substring(1)
        commend -> query.substring(1)
        else -> query
    }

    fun validate() {
        require(query.isNotBlank()) { "내용을 입력해주세요." }
    }
}
