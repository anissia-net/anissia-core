package anissia.domain.activePanel.service

import anissia.domain.activePanel.command.DoCommandActivePanelCommand
import anissia.domain.session.model.SessionItem
import reactor.core.publisher.Mono

interface ActivePanelCommandService {
    fun doCommand(cmd: DoCommandActivePanelCommand, sessionItem: SessionItem): Mono<String>
}
