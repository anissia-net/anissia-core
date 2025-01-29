package anissia.domain.translator.controller

import anissia.domain.translator.command.AddApplyCommand
import anissia.domain.translator.command.GetApplyCommand
import anissia.domain.translator.command.GetApplyListCommand
import anissia.domain.translator.command.NewApplyPollCommand
import anissia.domain.translator.model.TranslatorApplyItem
import anissia.domain.translator.service.TranslatorApplyService
import anissia.infrastructure.common.As
import anissia.shared.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/translator")
class TranslatorController(
    private val translatorApplyService: TranslatorApplyService,
) {
    @GetMapping("/apply/list/{page:\\d+}")
    fun getApplyList(cmd: GetApplyListCommand, exchange: ServerWebExchange): ApiResponse<Page<TranslatorApplyItem>> =
        ApiResponse.ok(translatorApplyService.getList(cmd))

    @GetMapping("/apply/{applyNo:\\d+}")
    fun getApply(cmd: GetApplyCommand, exchange: ServerWebExchange): ApiResponse<TranslatorApplyItem> =
        ApiResponse.ok(translatorApplyService.get(cmd))

    @GetMapping("/apply/count")
    fun getNewTranslatorApplyCount(exchange: ServerWebExchange): ApiResponse<Int> =
        ApiResponse.ok(translatorApplyService.getApplyingCount())

    @PostMapping("/apply")
    fun newApply(@RequestBody cmd: AddApplyCommand, exchange: ServerWebExchange): ApiResponse<Long> =
        translatorApplyService.add(cmd, exchange.sessionItem)

    @PostMapping("/apply/{applyNo:\\d+}/poll")
    fun newApplyPoll(@RequestBody cmd: NewApplyPollCommand, @PathVariable applyNo: Long, exchange: ServerWebExchange): ApiResponse<Void> =
        translatorApplyService.addPoll(cmd.apply { this.applyNo = applyNo }, exchange.sessionItem)
}
