package anissia.domain.activePanel.service

import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.command.AddTextActivePanelCommand
import anissia.domain.activePanel.command.DoCommandActivePanelCommand
import anissia.domain.activePanel.command.GetListActivePanelCommand
import anissia.domain.activePanel.model.ActivePanelItem
import anissia.domain.activePanel.repository.ActivePanelRepository
import anissia.domain.session.model.SessionItem
import anissia.infrastructure.common.As
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ActivePanelLogServiceImpl(
    private val activePanelRepository: ActivePanelRepository,
): ActivePanelLogService {
    override fun getList(cmd: GetListActivePanelCommand, sessionItem: SessionItem): Page<ActivePanelItem> {
        cmd.validate()
        return activePanelRepository.findAllByOrderByApNoDesc(PageRequest.of(cmd.page, 20))
            .run { if (cmd.mode == "admin" && sessionItem.isAdmin) this else As.filterPage(this) { it.published } }
            .map { ActivePanelItem(it) }
    }

    override fun addText(cmd: AddTextActivePanelCommand, sessionItem: SessionItem?): ResultWrapper<Unit> {
        activePanelRepository.save(ActivePanel(published = cmd.published, an = sessionItem?.an ?: sessionItem?.an ?: 0, code = "TEXT", data1 = cmd.text))
        return ResultWrapper.ok()
    }

    override fun addNotice(cmd: DoCommandActivePanelCommand, sessionItem: SessionItem): ResultWrapper<Unit> {
        activePanelRepository.save(ActivePanel(published = cmd.published, an = sessionItem.an, code = "TEXT", data1 = "《공지》 ${sessionItem.name} : ${cmd.text}"))
        return ResultWrapper.ok()
    }
}
