package anissia.domain.activePanel.service

import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.command.*
import anissia.domain.activePanel.model.*
import anissia.domain.session.model.SessionItem
import org.springframework.data.domain.Page
import reactor.core.publisher.Mono

interface ActivePanelService {
    fun getList(cmd: GetListActivePanelCommand, sessionItem: SessionItem): Mono<Page<ActivePanelItem>>
    fun doCommand(cmd: DoCommandActivePanelCommand, sessionItem: SessionItem): Mono<Void>
    fun addText(cmd: AddTextActivePanelCommand, sessionItem: SessionItem?): Mono<ActivePanel>
    fun addNotice(cmd: AddTextActivePanelCommand, sessionItem: SessionItem): Mono<ActivePanel>
    fun addDeleteTopic(cmd: AddDeleteTopicLogActivePanelCommand, sessionItem: SessionItem): Mono<ActivePanel>
    fun addDeletePost(cmd: AddDeletePostLogActivePanelCommand, sessionItem: SessionItem): Mono<ActivePanel>
}
