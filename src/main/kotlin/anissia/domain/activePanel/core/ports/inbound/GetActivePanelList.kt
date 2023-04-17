package anissia.domain.activePanel.core.ports.inbound


import anissia.domain.activePanel.core.model.ActivePanelItem
import anissia.domain.activePanel.core.model.GetActivePanelListCommand
import anissia.domain.session.core.model.Session
import org.springframework.data.domain.Page

interface GetActivePanelList {
    fun handle(cmd: GetActivePanelListCommand, session: Session): Page<ActivePanelItem>
}
