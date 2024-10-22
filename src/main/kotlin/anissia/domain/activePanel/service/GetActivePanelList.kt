package anissia.domain.activePanel.service


import anissia.domain.activePanel.model.ActivePanelItem
import anissia.domain.activePanel.model.GetActivePanelListCommand
import anissia.domain.session.model.Session
import org.springframework.data.domain.Page

interface GetActivePanelList {
    fun handle(cmd: GetActivePanelListCommand, session: Session): Page<ActivePanelItem>
}
