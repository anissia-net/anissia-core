package test.domain.translator

import anissia.domain.translator.service.TranslatorApplyService

class TranslatorTest(
    private val translatorApplyService: TranslatorApplyService,
) {
//    @GetMapping("/apply/list/{page:\\d+}")
//    fun getApplyList(cmd: GetApplyListCommand, exchange: ServerWebExchange): Mono<ApiResponse<Page<TranslatorApplyItem>> =
//        getApplyList.handle(cmd))
//
//    @GetMapping("/apply/{applyNo:\\d+}")
//    fun getApply(cmd: GetApplyCommand, exchange: ServerWebExchange): Mono<ApiResponse<TranslatorApplyItem> =
//        getApply.handle(cmd))
//
//    @GetMapping("/apply/count")
//    fun getNewTranslatorApplyCount(exchange: ServerWebExchange): Mono<ApiResponse<Int> =
//        getNewTranslatorApplyCount.handle())
//
//    @PostMapping("/apply")
//    fun newApply(@RequestBody cmd: NewApplyCommand, exchange: ServerWebExchange): Mono<ApiResponse<Long> =
//        newApply.handle(cmd, exchange.sessionItem)
//
//    @PostMapping("/apply/{applyNo:\\d+}/poll")
//    fun newApplyPoll(@RequestBody cmd: NewApplyPollCommand, @PathVariable applyNo: Long, exchange: ServerWebExchange): Mono<String> =
//        newApplyPoll.handle(cmd.apply { this.applyNo = applyNo }, exchange.sessionItem)
}
