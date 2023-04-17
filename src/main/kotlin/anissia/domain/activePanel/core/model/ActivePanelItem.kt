package anissia.domain.activePanel.core.model

import anissia.domain.activePanel.core.ActivePanel
import com.fasterxml.jackson.annotation.JsonInclude

data class ActivePanelItem (
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
        val regTime: Long = 0L
) {
        constructor(activePanel: ActivePanel): this(
                apNo = activePanel.apNo,
                published = activePanel.published,
                code = activePanel.code,
                status = activePanel.status,
                data1 = activePanel.data1,
                data2 = activePanel.data2,
                data3 = activePanel.data3,
                regTime = activePanel.regDt.toEpochSecond()
        )
}
