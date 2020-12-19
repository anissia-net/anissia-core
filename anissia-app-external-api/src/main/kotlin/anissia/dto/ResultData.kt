package anissia.dto

/**
 * basically web result
 */
data class ResultData<T>(
        /** code */
        var st: String,
        /** message */
        var msg: String? = "",
        /** data */
        var data: T? = null
) {
        companion object {
                /**
                 * data is exist : st = OK
                 * data is not exist : st = NULL
                 */
                fun <T> just(data: T) : ResultData<T> = ResultData(if (data != null) "OK" else "NULL", "", data)
        }
}