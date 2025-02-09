package anissia.domain.agenda.service

import anissia.domain.agenda.repository.AgendaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class AgendaServiceImpl(
    private val agendaRepository: AgendaRepository,
): AgendaService {
    @Transactional
    override fun deleteDeletePaddingAnime(): Mono<String> =
        Mono.fromCallable {
            agendaRepository.deleteDeletePadding()
            ""
        }
}
