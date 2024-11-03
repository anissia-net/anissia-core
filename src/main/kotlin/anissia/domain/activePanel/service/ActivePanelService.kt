package anissia.domain.activePanel.service


import anissia.domain.activePanel.model.ActivePanelItem
import anissia.domain.activePanel.command.AddTextActivePanelCommand
import anissia.domain.activePanel.command.DoCommandActivePanelCommand
import anissia.domain.activePanel.command.GetListActivePanelCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page

interface ActivePanelService {
    fun getList(cmd: GetListActivePanelCommand, session: Session): Page<ActivePanelItem>
    fun doCommand(cmd: DoCommandActivePanelCommand, session: Session): ResultWrapper<Unit>
    fun addText(cmd: AddTextActivePanelCommand, session: Session?): ResultWrapper<Unit>
}
