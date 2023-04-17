package anissia.shared

import com.fasterxml.jackson.annotation.JsonInclude

class ResultWrapper<T> private constructor(
        var code: String = "none",
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var message: String? = null,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var data: T? = null
) {
    companion object {
        fun ok() : ResultWrapper<Unit> = ResultWrapper("ok", null, null)
        fun fail(msg: String? = "") : ResultWrapper<Unit> = ResultWrapper("fail", msg, null)
        fun error(msg: String?) : ResultWrapper<Unit> = ResultWrapper("error", msg, null)

        fun <T> ok(data: T?) : ResultWrapper<T> = ResultWrapper("ok", null, data)
        fun <T> fail(msg: String, data: T?) : ResultWrapper<T> = ResultWrapper("fail", msg, data)
        fun <T> error(msg: String, data: T?) : ResultWrapper<T> = ResultWrapper("error", msg, data)

        fun <T> of(st: String, msg: String? = null, data: T? = null) : ResultWrapper<T> = ResultWrapper(st, msg, data)
    }
}
