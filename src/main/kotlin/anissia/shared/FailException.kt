package gs.shared

/**
 *
 */
open class FailException(override val message: String = "알수없는 오류입니다."): RuntimeException()
