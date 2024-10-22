package anissia.domain.agenda.service

import anissia.domain.agenda.repository.AgendaRepository
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
