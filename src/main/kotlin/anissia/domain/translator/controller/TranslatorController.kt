package anissia.domain.translator.controller

import anissia.domain.translator.command.AddApplyCommand
import anissia.domain.translator.command.GetApplyCommand
import anissia.domain.translator.command.GetApplyListCommand
import anissia.domain.translator.command.NewApplyPollCommand
import anissia.domain.translator.model.TranslatorApplyItem
import anissia.domain.translator.service.TranslatorApplyService
import anissia.infrastructure.common.As
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/translator")
class TranslatorController(
    private val translatorApplyService: TranslatorApplyService,
) {
    @GetMapping("/apply/list/{page:\\d+}")
    fun getApplyList(cmd: GetApplyListCommand, exchange: ServerWebExchange): ResultWrapper<Page<TranslatorApplyItem>> =
        ResultWrapper.ok(translatorApplyService.getList(cmd))

    @GetMapping("/apply/{applyNo:\\d+}")
    fun getApply(cmd: GetApplyCommand, exchange: ServerWebExchange): ResultWrapper<TranslatorApplyItem> =
        ResultWrapper.ok(translatorApplyService.get(cmd))

    @GetMapping("/apply/count")
    fun getNewTranslatorApplyCount(exchange: ServerWebExchange): ResultWrapper<Int> =
        ResultWrapper.ok(translatorApplyService.getApplyingCount())

    @PostMapping("/apply")
    fun newApply(@RequestBody cmd: AddApplyCommand, exchange: ServerWebExchange): ResultWrapper<Long> =
        translatorApplyService.add(cmd, As.toSession(exchange))

    @PostMapping("/apply/{applyNo:\\d+}/poll")
    fun newApplyPoll(@RequestBody cmd: NewApplyPollCommand, @PathVariable applyNo: Long, exchange: ServerWebExchange): ResultWrapper<Unit> =
        translatorApplyService.addPoll(cmd.apply { this.applyNo = applyNo }, As.toSession(exchange))
}
