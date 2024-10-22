package test.domain.activePanel

import anissia.domain.activePanel.service.GetActivePanelList
import anissia.domain.activePanel.service.NewActivePanelNotice

class ActivePanelTest(
    private val getActivePanelList: GetActivePanelList,
    private val newActivePanelNotice: NewActivePanelNotice,
) {
//    @GetMapping("/list/{page:[\\d]+}")
//    fun getList(cmd: GetActivePanelListCommand, exchange: ServerWebExchange): ResultWrapper<Page<ActivePanelItem>> =
//        ResultWrapper.ok(getActivePanelList.handle(cmd, As.toSession(exchange)))
//
//    @PostMapping("/notice")
//    fun newNotice(@RequestBody cmd: NewActivePanelNoticeCommand, exchange: ServerWebExchange): ResultWrapper<Unit> =
//        newActivePanelNotice.handle(cmd, As.toSession(exchange))
}
