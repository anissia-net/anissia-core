package anissia.domain.activePanel.application

import anissia.domain.activePanel.core.model.ActivePanelItem
import anissia.domain.activePanel.core.model.GetActivePanelListCommand
import anissia.domain.activePanel.core.model.NewActivePanelNoticeCommand
import anissia.domain.activePanel.core.ports.inbound.GetActivePanelList
import anissia.domain.activePanel.core.ports.inbound.NewActivePanelNotice
import anissia.infrastructure.common.As
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/active-panel")
class ActivePanelController(
    private val getActivePanelList: GetActivePanelList,
    private val newActivePanelNotice: NewActivePanelNotice,
) {
    @GetMapping("/list/{page:[\\d]+}")
    fun getList(cmd: GetActivePanelListCommand, exchange: ServerWebExchange): ResultWrapper<Page<ActivePanelItem>> =
        ResultWrapper.ok(getActivePanelList.handle(cmd, As.toSession(exchange)))

    @PostMapping("/notice")
    fun newNotice(@RequestBody cmd: NewActivePanelNoticeCommand, exchange: ServerWebExchange): ResultWrapper<Unit> =
        newActivePanelNotice.handle(cmd, As.toSession(exchange))
}
