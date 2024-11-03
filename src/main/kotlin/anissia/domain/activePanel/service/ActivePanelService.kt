package anissia.domain.activePanel.service


import anissia.domain.activePanel.model.ActivePanelItem
import anissia.domain.activePanel.command.AddTextActivePanelCommand
import anissia.domain.activePanel.command.DoCommandActivePanelCommand
import anissia.domain.activePanel.command.GetListActivePanelCommand
import anissia.domain.session.model.SessionItem
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page

interface ActivePanelService {
    fun getList(cmd: GetListActivePanelCommand, sessionItem: SessionItem): Page<ActivePanelItem>
    fun doCommand(cmd: DoCommandActivePanelCommand, sessionItem: SessionItem): ResultWrapper<Unit>
    fun addText(cmd: AddTextActivePanelCommand, sessionItem: SessionItem?): ResultWrapper<Unit>
}
