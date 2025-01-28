package anissia.shared

open class ApiFailException(
    override val message: String? = "알수없는 오류가 발생하였습니다.",
    private val data: Any? = null
): ApiException("fail", message, data)
