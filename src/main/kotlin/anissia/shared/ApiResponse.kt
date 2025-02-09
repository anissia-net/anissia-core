package anissia.shared

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude

class ApiResponse<T> private constructor(
    val code: String,
    @get:JsonInclude(JsonInclude.Include.NON_EMPTY)
    val message: String?,
    @get:JsonInclude(JsonInclude.Include.NON_EMPTY)
    val data: T?
) {
    companion object {
        fun <T> ok() : ApiResponse<T> = ApiResponse("ok", null, null)
        fun <T> ok(data: T?) : ApiResponse<T> = ApiResponse("ok", null, data)

        fun <T> fail(message: String?) : ApiResponse<T> = ApiResponse("fail", message, null)
        fun <T> fail(message: String, data: T?) : ApiResponse<T> = ApiResponse("fail", message, data)

        fun <T> error() : ApiResponse<T> = ApiResponse("error", "", null)
        fun <T> error(message: String?) : ApiResponse<T> = ApiResponse("error", message, null)
        fun <T> error(message: String, data: T?) : ApiResponse<T> = ApiResponse("error", message, data)

        fun <T> of(code: String, message: String?) : ApiResponse<T> = ApiResponse(code, message, null)
        fun <T> of(code: String, message: String?, data: T?) : ApiResponse<T> = ApiResponse(code, message, data)
    }

    @get:JsonIgnore
    val success: Boolean get() = code == "ok"
}
