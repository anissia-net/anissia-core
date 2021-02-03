package anissia.rdb.dto.request

import javax.validation.constraints.NotBlank

data class ActivePanelNoticeRequest (
    @field:NotBlank(message = "내용을 입력해주세요.")
    var text: String = "",
    var published: Boolean
)
