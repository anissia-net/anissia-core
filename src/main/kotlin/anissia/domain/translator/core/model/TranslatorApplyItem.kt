package anissia.domain.translator.core.model


import anissia.domain.agenda.core.Agenda

class TranslatorApplyItem(
    val applyNo: Long = 0,
    val status: String? = "",
    val result: String? = "",
    val name: String? = null,
    val website: String? = null,
    val regTime: Long = 0L,
    val polls: List<TranslatorApplyPollItem> = listOf()
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
