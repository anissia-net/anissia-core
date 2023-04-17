package anissia.domain.activePanel.core.ports.outbound

import anissia.domain.activePanel.core.ActivePanel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ActivePanelRepository : JpaRepository<ActivePanel, Long> { //, QuerydslPredicateExecutor<ActivePanel> {

    fun findAllByOrderByApNoDesc(pageable: Pageable): Page<ActivePanel>
}
