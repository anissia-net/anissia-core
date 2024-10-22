package anissia.domain.agenda.repository

import anissia.domain.agenda.AgendaPoll
import org.springframework.data.jpa.repository.JpaRepository

interface AgendaPollRepository : JpaRepository<AgendaPoll, Long> { //, QuerydslPredicateExecutor<AgendaPoll> {


}
