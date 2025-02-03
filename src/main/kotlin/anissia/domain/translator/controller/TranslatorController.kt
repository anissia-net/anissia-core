package anissia.domain.translator.controller

import anissia.domain.translator.command.AddApplyCommand
import anissia.domain.translator.command.GetApplyCommand
import anissia.domain.translator.command.GetApplyListCommand
import anissia.domain.translator.command.NewApplyPollCommand
import anissia.domain.translator.model.TranslatorApplyItem
import anissia.domain.translator.service.TranslatorApplyService
import anissia.infrastructure.common.sessionItem
import anissia.infrastructure.common.toApiResponse
import anissia.shared.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/translator")
class TranslatorController(
    private val translatorApplyService: TranslatorApplyService,
) {
    @GetMapping("/apply/list/{page:\\d+}")
    fun getApplyList(cmd: GetApplyListCommand, exchange: ServerWebExchange): Mono<ApiResponse<Page<TranslatorApplyItem>>> =
        translatorApplyService.getList(cmd).toApiResponse

    @GetMapping("/apply/{applyNo:\\d+}")
    fun getApply(cmd: GetApplyCommand, exchange: ServerWebExchange): Mono<ApiResponse<TranslatorApplyItem>> =
        translatorApplyService.get(cmd).toApiResponse

    @GetMapping("/apply/count")
    fun getNewTranslatorApplyCount(exchange: ServerWebExchange): Mono<ApiResponse<Int>> =
        translatorApplyService.getApplyingCount().toApiResponse

    @PostMapping("/apply")
    fun newApply(@RequestBody cmd: AddApplyCommand, exchange: ServerWebExchange): Mono<ApiResponse<Long>> =
        translatorApplyService.add(cmd, exchange.sessionItem).toApiResponse

    @PostMapping("/apply/{applyNo:\\d+}/poll")
    fun newApplyPoll(@RequestBody cmd: NewApplyPollCommand, @PathVariable applyNo: Long, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        translatorApplyService.addPoll(cmd.apply { this.applyNo = applyNo }, exchange.sessionItem).toApiResponse
}
