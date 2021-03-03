package anissia.dto

import anissia.rdb.domain.Agenda
import java.time.LocalDateTime


data class TranslatorApplyDto (
        var applyNo: Long = 0,
        var status: String = "",
        var an: Long = 0,
        var data1: String? = null,
        var data2: String? = null,
        var data3: String? = null,
        var regDt: LocalDateTime = LocalDateTime.now(),
) {
    constructor(agenda: Agenda, includePolls: Boolean = false): this(
            applyNo = agenda.agendaNo,
            status = agenda.status,
            an = agenda.an,
            data1 = agenda.data1,
            data2 = agenda.data2,
            data3 = agenda.data3,
            regDt = agenda.regDt,
    )
}

