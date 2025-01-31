package anissia.domain.activePanel.repository

import anissia.domain.activePanel.ActivePanel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

interface ActivePanelRepository : ReactiveCrudRepository<ActivePanel, Long> {

    fun findAllByOrderByApNoDesc(pageable: Pageable): Mono<Page<ActivePanel>>

    @Transactional
    @Modifying
    @Query("DELETE FROM ActivePanel WHERE regDt < :regDt")
    fun deleteAllByRegDtBefore(regDt: OffsetDateTime = OffsetDateTime.now().minusDays(90)): Mono<Int>
}
