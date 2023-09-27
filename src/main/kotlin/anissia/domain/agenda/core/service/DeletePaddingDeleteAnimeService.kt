package anissia.domain.agenda.core.service

import anissia.domain.agenda.core.ports.inbound.DeletePaddingDeleteAnime
import anissia.domain.agenda.core.ports.outbound.AgendaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePaddingDeleteAnimeService(
    private val agendaRepository: AgendaRepository,
): DeletePaddingDeleteAnime {

    @Transactional
    override fun handle() {
        agendaRepository.deletePaddingDeleteAnime()
    }

}
