package anissia.domain.agenda.repository

import anissia.domain.agenda.Agenda
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

interface AgendaRepository : ReactiveCrudRepository<Agenda, Long> {

    fun findAllByCodeAndStatusOrderByAgendaNoDesc(code: String, status: String, pageable: Pageable = PageRequest.of(0, 100)): Mono<Page<Agenda>>

    fun findAllByCodeOrderByStatusAscAgendaNoDesc(code: String, pageable: Pageable = PageRequest.of(0, 100)): Mono<Page<Agenda>>

    fun countByCodeAndStatus(code: String, status: String): Mono<Int>

    fun existsByCodeAndStatusAndAn(code: String, status: String, an: Long): Mono<Boolean>

    fun existsByCodeAndStatusAndAnAndUpdDtAfter(code: String, status: String, an: Long, updDt: OffsetDateTime): Mono<Boolean>

    @Modifying
    @Query("DELETE FROM Agenda a WHERE a.code = 'ANIME-DEL' and a.updDt < :baseDateTime")
    fun deleteDeletePadding(baseDateTime: OffsetDateTime = OffsetDateTime.now().minusDays(30)): Mono<Int>

    @EntityGraph(attributePaths = ["polls"])
    fun findWithPollsByAgendaNoAndCode(agendaNo: Long, code: String): Mono<Agenda>

    @Query("SELECT a FROM Agenda a WHERE a.an = :an AND a.code = 'TRANSLATOR-APPLY' and a.status = 'DONE' ORDER BY a.agendaNo DESC")
    fun findPassedTranslatorApply(an: Long): Flux<Agenda>
}
