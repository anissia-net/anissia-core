package anissia.controller

import anissia.dto.request.ActivePanelNoticeRequest
import anissia.services.ActivePanelService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/active-panel")
class ActivePanelController(
    private val activePanelService: ActivePanelService
) {
    @GetMapping("/list/{page:[\\d]+}")
    fun getList(@RequestParam mode: String, @PathVariable page: Int) = activePanelService.getList(mode == "admin", page)

    @PostMapping("/notice")
    fun addNotice(@RequestBody apnr: ActivePanelNoticeRequest) = activePanelService.saveNotice(apnr)
}