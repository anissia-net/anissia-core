package anissia.dto

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * basically web result
 */
data class ResultStatus(
        /** st */
        var st: String,
        /** message */
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        var msg: String? = ""
)