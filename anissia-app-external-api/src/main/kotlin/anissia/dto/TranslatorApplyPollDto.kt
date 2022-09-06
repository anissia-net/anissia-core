package anissia.dto

import anissia.rdb.entity.AgendaPoll
import java.time.LocalDateTime

data class TranslatorApplyPollDto (
        var no: Long = 0,
        var vote: Int = 0,
        var name: String? = null,
        var comment: String? = null,
        var regDt: LocalDateTime = LocalDateTime.now(),
) {
    constructor(agendaPoll: AgendaPoll): this(
            no = agendaPoll.pollNo,
            vote = agendaPoll.voteUp + agendaPoll.voteDown,
            name = agendaPoll.name,
            comment = agendaPoll.comment,
            regDt = agendaPoll.regDt,
    )
}

