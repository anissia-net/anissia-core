package anissia.domain.agenda.service

import anissia.domain.agenda.repository.AgendaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AgendaServiceImpl(
    private val agendaRepository: AgendaRepository,
): AgendaService {

    @Transactional
    override fun deleteDeletePaddingAnime() {
        agendaRepository.deleteDeletePadding()
    }

}
