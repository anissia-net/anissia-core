package anissia.domain.activePanel.service

import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.command.GetListActivePanelCommand
import anissia.domain.activePanel.model.ActivePanelItem
import anissia.domain.activePanel.repository.ActivePanelRepository
import anissia.domain.session.model.SessionItem
import anissia.infrastructure.common.filterPage
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActivePanelServiceImpl(
    private val activePanelRepository: ActivePanelRepository,
): ActivePanelService {

    override fun getList(cmd: GetListActivePanelCommand, sessionItem: SessionItem): Mono<Page<ActivePanelItem>> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .map {
                activePanelRepository.findAllByOrderByApNoDesc(PageRequest.of(cmd.page, 20))
                    .run { if (cmd.mode == "admin" && sessionItem.isAdmin) this else this.filterPage { it.published } }
            }
            .map { activePanel -> activePanel.map { ActivePanelItem(it) } }

    override fun add(activePanel: ActivePanel): Mono<ActivePanel> =
        Mono.just(activePanel)
            .map { activePanelRepository.save(it) }
}
