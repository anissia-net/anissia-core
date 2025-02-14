package anissia.domain.agenda.repository

import anissia.domain.agenda.Agenda
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.OffsetDateTime

interface AgendaRepository : JpaRepository<Agenda, Long> { //, QuerydslPredicateExecutor<Agenda> {

    fun findAllByCodeAndStatusOrderByAgendaNoDesc(code: String, status: String, pageable: Pageable = PageRequest.of(0, 100)): Page<Agenda>

    fun findAllByCodeOrderByStatusAscAgendaNoDesc(code: String, pageable: Pageable = PageRequest.of(0, 100)): Page<Agenda>

    fun countByCodeAndStatus(code: String, status: String): Int

    fun existsByCodeAndStatusAndAn(code: String, status: String, an: Long): Boolean

    fun existsByCodeAndStatusAndAnAndUpdDtAfter(code: String, status: String, an: Long, updDt: OffsetDateTime): Boolean

    @Modifying
    @Query("DELETE FROM Agenda a WHERE a.code = 'ANIME-DEL' and a.updDt < :baseDateTime")
    fun deleteDeletePadding(baseDateTime: OffsetDateTime = OffsetDateTime.now().minusDays(30)): Int

    @EntityGraph(attributePaths = ["polls"])
    fun findWithPollsByAgendaNoAndCode(agendaNo: Long, code: String): Agenda?

    @Query("SELECT a FROM Agenda a WHERE a.an = :an AND a.code = 'TRANSLATOR-APPLY' and a.status = 'DONE' ORDER BY a.agendaNo DESC")
    fun findPassedTranslatorApply(an: Long, pageable: Pageable = PageRequest.of(0, 1)): Agenda?
}
