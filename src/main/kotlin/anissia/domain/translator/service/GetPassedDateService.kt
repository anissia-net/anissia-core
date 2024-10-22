package anissia.domain.translator.service

import anissia.domain.agenda.repository.AgendaRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class GetPassedDateService(
    private val agendaRepository: AgendaRepository
): GetPassedDate {
    override fun handle(an: Long): OffsetDateTime? =
        agendaRepository.findPassedTranslatorApply(an).firstOrNull()?.updDt
}
