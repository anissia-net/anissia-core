package anissia.dto.request

import javax.validation.constraints.NotBlank

data class ActivePanelNoticeRequest (
    @field:NotBlank(message = "내용을 입력해주세요.")
    var query: String = ""
) {
    val published get() = query.startsWith("!")
    val commend get() = query.startsWith("/")

    val text get() = when {
        published -> query.substring(1)
        commend -> query.substring(1)
        else -> query
    }
}
