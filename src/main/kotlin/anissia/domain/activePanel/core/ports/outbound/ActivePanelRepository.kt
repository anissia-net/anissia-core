package anissia.domain.activePanel.core.ports.outbound

import anissia.domain.activePanel.core.ActivePanel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

interface ActivePanelRepository : JpaRepository<ActivePanel, Long> { //, QuerydslPredicateExecutor<ActivePanel> {

    fun findAllByOrderByApNoDesc(pageable: Pageable): Page<ActivePanel>

    @Transactional
    @Modifying
    @Query("DELETE FROM ActivePanel WHERE regDt < :regDt")
    fun deleteAllByRegDtBefore(regDt: OffsetDateTime = OffsetDateTime.now().minusDays(90))
}
