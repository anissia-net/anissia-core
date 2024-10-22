package anissia.domain.translator.service

import anissia.domain.agenda.repository.AgendaRepository
import anissia.domain.translator.infrastructure.ApplyValue
import org.springframework.stereotype.Service

@Service
class GetNewTranslatorApplyCountService(
    private val agendaRepository: AgendaRepository,
): GetNewTranslatorApplyCount {
    override fun handle(): Int =
        agendaRepository.countByCodeAndStatus(ApplyValue.CODE, "ACT")

}
