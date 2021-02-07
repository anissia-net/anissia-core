package anissia.services

import anissia.rdb.repository.AgendaPollsRepository
import anissia.rdb.repository.AgendaRepository
import org.springframework.stereotype.Service

@Service
class AgendaService(
    private val agendaRepository: AgendaRepository,
    private val agendaPollsRepository: AgendaPollsRepository
) {

}