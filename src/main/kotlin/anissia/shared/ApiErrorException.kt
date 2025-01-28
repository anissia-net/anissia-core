package anissia.shared

open class ApiErrorException(
    override val message: String?
): ApiException("fail", message)
