package anissia.shared

open class ApiException(
    private val code: String,
    override val message: String?,
): RuntimeException() {
    fun <T> toResult(): ApiResponse<T> = ApiResponse.of(code, message)
}
