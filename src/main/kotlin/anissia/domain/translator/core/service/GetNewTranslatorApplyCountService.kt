package anissia.domain.translator.core.service

import anissia.domain.agenda.core.ports.outbound.AgendaRepository
import anissia.domain.translator.core.ports.inbound.GetNewTranslatorApplyCount
import anissia.domain.translator.infrastructure.ApplyValue
import org.springframework.stereotype.Service

@Service
class GetNewTranslatorApplyCountService(
    private val agendaRepository: AgendaRepository,
): GetNewTranslatorApplyCount {
    override fun handle(): Int =
        agendaRepository.countByCodeAndStatus(ApplyValue.CODE, "ACT")

}
