package anissia.shared

open class ApiException(
    private val code: String,
    override val message: String?,
    private val data: Any? = null
): RuntimeException() {
    fun toResult(): ApiResponse<Any?> = ApiResponse.of(code, message, data)
}
