package anissia.shared

open class ApiException(
    private val code: String,
    override val message: String?,
    private val data: Any? = null
): RuntimeException() {
    fun toResult(): ApiResponse<Any?> = ApiResponse.of(code, message, data)

    companion object {
        fun ok() : ApiException = of(ApiResponse.ok())
        fun ok(data: Any?) : ApiException = of(ApiResponse.ok(data))

        fun fail(message: String?) : ApiException = of(ApiResponse.fail(message))
        fun fail(message: String, data: Any?) : ApiException = of(ApiResponse.fail(message, data))

        fun error() : ApiException = of(ApiResponse.error())
        fun error(message: String?) : ApiException = of(ApiResponse.error(message))
        fun error(message: String, data: Any?) : ApiException = of(ApiResponse.error(message, data))

        fun of(code: String, message: String?) : ApiException = of(ApiResponse.of(code, message))
        fun of(code: String, message: String?, data: Any?) : ApiException = of(ApiResponse.of(code, message, data))

        fun of(apiResponse: ApiResponse<Any?>): ApiException =
            ApiException(apiResponse.code, apiResponse.message, apiResponse.data)
    }
}
