package anissia.domain.agenda.core.ports.outbound

import anissia.domain.agenda.core.AgendaPoll
import org.springframework.data.jpa.repository.JpaRepository

interface AgendaPollRepository : JpaRepository<AgendaPoll, Long> { //, QuerydslPredicateExecutor<AgendaPoll> {


}
