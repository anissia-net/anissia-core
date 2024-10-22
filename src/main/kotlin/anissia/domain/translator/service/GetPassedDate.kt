package anissia.domain.translator.service

import java.time.OffsetDateTime

interface GetPassedDate {
    fun handle(an: Long): OffsetDateTime?
}
