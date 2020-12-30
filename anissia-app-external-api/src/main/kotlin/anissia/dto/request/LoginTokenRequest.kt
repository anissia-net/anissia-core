package anissia.dto.request

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/**
 * this token part is used by auto login (is not user view part)
 * because it does not output an error message
 */
data class LoginTokenRequest (
    @field:NotNull(message = "")
    @field:Size(min = 128, max = 550, message = "")
    @field:Pattern(regexp = "[\\d]{1,20}-[\\da-zA-Z]{128,512}", message = "")
    var absoluteToken: String
) {
    /** token number */
    val tokenNo get() = absoluteToken.substringBefore('-', "0").toLong()

    /** token */
    val token get() = absoluteToken.substringAfter('-', "")
}
