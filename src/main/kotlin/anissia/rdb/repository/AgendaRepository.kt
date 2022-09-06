package anissia.rdb.repository

import anissia.rdb.entity.Agenda
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.time.LocalDateTime

interface AgendaRepository : JpaRepository<Agenda, Long>, QuerydslPredicateExecutor<Agenda> {

    fun findAllByCodeAndStatusOrderByAgendaNoDesc(code: String, status: String, pageable: Pageable = PageRequest.of(0, 100)): Page<Agenda>

    fun findAllByCodeOrderByStatusAscAgendaNoDesc(code: String, pageable: Pageable = PageRequest.of(0, 100)): Page<Agenda>

    fun countByCodeAndStatus(code: String, status: String): Int

    fun existsByCodeAndStatusAndAn(code: String, status: String, an: Long): Boolean

    fun existsByCodeAndStatusAndAnAndUpdDtAfter(code: String, status: String, an: Long, updDt: LocalDateTime): Boolean

    @EntityGraph(attributePaths = ["polls"])
    fun findWithPollsByAgendaNoAndCode(agendaNo: Long, code: String): Agenda?
}
