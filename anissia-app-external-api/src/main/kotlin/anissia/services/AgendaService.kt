package anissia.services

import anissia.rdb.repository.AgendaPollRepository
import anissia.rdb.repository.AgendaRepository
import org.springframework.stereotype.Service

@Service
class AgendaService(
    private val agendaRepository: AgendaRepository,
    private val agendaPollRepository: AgendaPollRepository
) {

}