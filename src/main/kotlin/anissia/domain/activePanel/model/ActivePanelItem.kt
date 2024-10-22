package anissia.domain.activePanel.model

import anissia.domain.activePanel.ActivePanel
import com.fasterxml.jackson.annotation.JsonInclude

class ActivePanelItem (
    val apNo: Long,
    val published: Boolean,
    val code: String,
    val status: String,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val data1: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val data2: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val data3: String?,
    val regTime: Long,
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
