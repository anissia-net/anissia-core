package anissia.dto

import anissia.rdb.entity.ActivePanel
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

data class ActivePanelDto (
        var apNo: Long = 0,
        var published: Boolean = false,
        var code: String = "",
        var status: String = "",
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var data1: String? = null,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var data2: String? = null,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var data3: String? = null,
        var regDt: LocalDateTime = LocalDateTime.now()
) {
        constructor(activePanel: ActivePanel): this(
                apNo = activePanel.apNo,
                published = activePanel.published,
                code = activePanel.code,
                status = activePanel.status,
                data1 = activePanel.data1,
                data2 = activePanel.data2,
                data3 = activePanel.data3,
                regDt = activePanel.regDt
        )
}