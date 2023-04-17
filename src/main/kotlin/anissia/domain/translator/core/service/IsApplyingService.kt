package anissia.domain.translator.core.service

import anissia.domain.agenda.core.ports.outbound.AgendaRepository
import anissia.domain.session.core.model.Session
import anissia.domain.translator.core.ports.inbound.IsApplying
import anissia.domain.translator.infrastructure.ApplyValue
import org.springframework.stereotype.Service

@Service
class IsApplyingService(
    private val agendaRepository: AgendaRepository
): IsApplying {
    override fun handle(session: Session): Boolean {
        return agendaRepository.existsByCodeAndStatusAndAn(ApplyValue.CODE, "ACT", session.an)
    }
}
