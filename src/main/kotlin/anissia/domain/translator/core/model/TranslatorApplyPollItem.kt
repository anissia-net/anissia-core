package anissia.domain.translator.core.model

import anissia.domain.agenda.core.AgendaPoll

class TranslatorApplyPollItem (
    val no: Long = 0,
    val vote: Int = 0,
    val name: String? = null,
    val comment: String? = null,
    val regTime: Long = 0L,
) {
    constructor(agendaPoll: AgendaPoll): this(
        no = agendaPoll.pollNo,
        vote = agendaPoll.voteUp + agendaPoll.voteDown,
        name = agendaPoll.name,
        comment = agendaPoll.comment,
        regTime = agendaPoll.regDt.toEpochSecond(),
    )
}

