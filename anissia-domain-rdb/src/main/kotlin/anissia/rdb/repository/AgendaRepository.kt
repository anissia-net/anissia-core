package anissia.rdb.repository

import anissia.rdb.domain.Agenda
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AgendaRepository : JpaRepository<Agenda, Long>, QuerydslPredicateExecutor<Agenda> {


}
