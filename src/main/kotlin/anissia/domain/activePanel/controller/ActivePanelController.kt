package anissia.domain.activePanel.controller

import anissia.domain.activePanel.command.DoCommandActivePanelCommand
import anissia.domain.activePanel.command.GetListActivePanelCommand
import anissia.domain.activePanel.model.ActivePanelItem
import anissia.domain.activePanel.service.ActivePanelCommandService
import anissia.domain.activePanel.service.ActivePanelLogService
import anissia.infrastructure.common.As
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/active-panel")
class ActivePanelController(
    private val activePanelLogService: ActivePanelLogService,
    private val activePanelCommandService: ActivePanelCommandService,
) {
    @GetMapping("/list/{page:[\\d]+}")
    fun getList(cmd: GetListActivePanelCommand, exchange: ServerWebExchange): ResultWrapper<Page<ActivePanelItem>> =
        ResultWrapper.ok(activePanelLogService.getList(cmd, As.toSession(exchange)))

    @PostMapping("/command")
    fun doCommand(@RequestBody cmd: DoCommandActivePanelCommand, exchange: ServerWebExchange): ResultWrapper<Unit> =
        activePanelCommandService.doCommand(cmd, As.toSession(exchange))
}
