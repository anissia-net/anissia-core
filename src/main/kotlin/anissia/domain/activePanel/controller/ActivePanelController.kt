package anissia.domain.activePanel.controller

import anissia.domain.activePanel.command.DoCommandActivePanelCommand
import anissia.domain.activePanel.command.GetListActivePanelCommand
import anissia.domain.activePanel.model.ActivePanelItem
import anissia.domain.activePanel.service.ActivePanelService
import anissia.infrastructure.common.sessionItem
import anissia.infrastructure.common.toApiResponse
import anissia.shared.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/active-panel")
class ActivePanelController(
    private val activePanelService: ActivePanelService,
) {
    @GetMapping("/list/{page:[\\d]+}")
    fun getList(cmd: GetListActivePanelCommand, exchange: ServerWebExchange): Mono<ApiResponse<Page<ActivePanelItem>>> =
        activePanelService.getList(cmd, exchange.sessionItem).toApiResponse

    @PostMapping("/command")
    fun doCommand(@RequestBody cmd: DoCommandActivePanelCommand, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        activePanelService.doCommand(cmd, exchange.sessionItem).toApiResponse
}
