package anissia.rdb.repository

import anissia.rdb.domain.Agenda
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AgendaRepository : JpaRepository<Agenda, Long>, QuerydslPredicateExecutor<Agenda> {

    fun findAllByCodeAndStatusOrderByAgendaNoDesc(code: String, status: String, pageable: Pageable = PageRequest.of(0, 100)): Page<Agenda>
}
