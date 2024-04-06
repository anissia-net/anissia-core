package anissia.domain.translator.core.service

import anissia.domain.agenda.core.ports.outbound.AgendaRepository
import anissia.domain.translator.core.ports.inbound.GetPassedDate
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class GetPassedDateService(
    private val agendaRepository: AgendaRepository
): GetPassedDate {
    override fun handle(an: Long): OffsetDateTime? =
        agendaRepository.findPassedTranslatorApply(an).firstOrNull()?.updDt
}
