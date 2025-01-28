package anissia.domain.agenda.repository

import anissia.domain.agenda.AgendaPoll
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface AgendaPollRepository : ReactiveCrudRepository<AgendaPoll, Long>
