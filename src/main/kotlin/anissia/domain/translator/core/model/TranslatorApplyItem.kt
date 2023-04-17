package anissia.domain.translator.core.model


import anissia.domain.agenda.core.Agenda

class TranslatorApplyItem(
    var applyNo: Long = 0,
    var status: String? = "",
    var result: String? = "",
    var name: String? = null,
    var website: String? = null,
    val regTime: Long = 0L,
    var polls: List<TranslatorApplyPollItem> = listOf()
) {
    constructor(agenda: Agenda, includePolls: Boolean = false): this(
        applyNo = agenda.agendaNo,
        status = agenda.status,
        result = agenda.data1,
        name = agenda.data2,
        website = agenda.data3,
        regTime = agenda.regDt.toEpochSecond(),
        polls = if (includePolls) agenda.polls.map { TranslatorApplyPollItem(it) }.sortedBy { it.no } else listOf()
    )
}
