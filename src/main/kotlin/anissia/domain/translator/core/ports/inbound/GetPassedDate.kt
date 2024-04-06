package anissia.domain.translator.core.ports.inbound

import java.time.OffsetDateTime

interface GetPassedDate {
    fun handle(an: Long): OffsetDateTime?
}
