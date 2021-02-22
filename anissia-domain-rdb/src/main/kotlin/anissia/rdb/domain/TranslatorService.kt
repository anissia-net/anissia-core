package anissia.rdb.domain

import anissia.rdb.repository.AgendaRepository
import org.springframework.stereotype.Service

@Service
class TranslatorService(
    private val agendaRepository: AgendaRepository
) {

}