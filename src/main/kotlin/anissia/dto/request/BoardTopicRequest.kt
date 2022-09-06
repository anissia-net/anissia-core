package anissia.dto.request

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class BoardTopicRequest (
    @field:NotEmpty(message = "제목을 입력해주세요.")
    @field:Size(min = 1, max = 64, message = "제목을 입력해주세요.")
    var topic: String = "",

    @field:NotEmpty(message = "내용을 입력해주세요.")
    var content: String = "",
)
