package anissia.domain.translator.core.model

import anissia.domain.agenda.core.AgendaPoll

data class TranslatorApplyPollItem (
        var no: Long = 0,
        var vote: Int = 0,
        var name: String? = null,
        var comment: String? = null,
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

