package anissia.domain.activePanel.controller

import anissia.domain.activePanel.model.ActivePanelItem
import anissia.domain.activePanel.model.GetActivePanelListCommand
import anissia.domain.activePanel.model.NewActivePanelNoticeCommand
import anissia.domain.activePanel.service.GetActivePanelList
import anissia.domain.activePanel.service.NewActivePanelNotice
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
