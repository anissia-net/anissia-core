package anissia.dto

import anissia.rdb.entity.Agenda
import java.time.LocalDateTime


data class TranslatorApplyDto (
        var applyNo: Long = 0,
        var status: String? = "",
        var result: String? = "",
        var name: String? = null,
        var website: String? = null,
        var regDt: LocalDateTime = LocalDateTime.now(),
        var polls: List<TranslatorApplyPollDto> = listOf()
) {
    constructor(agenda: Agenda, includePolls: Boolean = false): this(
            applyNo = agenda.agendaNo,
            status = agenda.status,
            result = agenda.data1,
            name = agenda.data2,
            website = agenda.data3,
            regDt = agenda.regDt,
            polls = if (includePolls) agenda.polls.map { TranslatorApplyPollDto(it) }.sortedBy { it.no } else listOf()
    )
}

