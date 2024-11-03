package anissia.domain.translator.service

import anissia.domain.agenda.repository.AgendaRepository
import anissia.domain.session.model.SessionItem
import anissia.domain.translator.infrastructure.ApplyValue
import org.springframework.stereotype.Service

@Service
class IsApplyingService(
    private val agendaRepository: AgendaRepository
): IsApplying {
    override fun handle(sessionItem: SessionItem): Boolean {
        return agendaRepository.existsByCodeAndStatusAndAn(ApplyValue.CODE, "ACT", sessionItem.an)
    }
}
