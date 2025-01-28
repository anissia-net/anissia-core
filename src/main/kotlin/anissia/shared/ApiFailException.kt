package anissia.shared

open class ApiFailException(
    override val message: String? = "알수없는 오류가 발생하였습니다."
): ApiException("fail", message)
