package anissia.domain.activePanel.core.service

import anissia.domain.activePanel.core.model.ActivePanelItem
import anissia.domain.activePanel.core.model.GetActivePanelListCommand
import anissia.domain.activePanel.core.ports.inbound.GetActivePanelList
import anissia.domain.activePanel.core.ports.outbound.ActivePanelRepository
import anissia.domain.session.core.model.Session
import anissia.infrastructure.common.As
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class GetActivePanelListService(
    private val activePanelRepository: ActivePanelRepository,
): GetActivePanelList {
    override fun handle(cmd: GetActivePanelListCommand, session: Session): Page<ActivePanelItem> {
        cmd.validate()

        return activePanelRepository.findAllByOrderByApNoDesc(PageRequest.of(cmd.page, 20))
            .run { if (cmd.mode == "admin" && session.isAdmin) this else As.filterPage(this) { it.published } }
            .map { ActivePanelItem(it) }
    }
}
