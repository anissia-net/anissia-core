package anissia.shared

open class ApiErrorException(
    override val message: String?,
    private val data: Any? = null
): ApiException("fail", message, data)
