package test.domain.anime


import anissia.domain.anime.service.CaptionService

class AnimeCaptionTest(
    private val captionService: CaptionService,
) {
//    @GetMapping("/caption/animeNo/{animeNo:\\d+}")
//    fun getCaptionListByAnimeNo(cmd: GetCaptionListByAnimeNoCommand, exchange: ServerWebExchange): Mono<ApiResponse<List<CaptionItem>> =
//        getCaptionListByAnimeNo.handle(cmd, exchange.sessionItem))
//
//    @GetMapping("/caption/myList/{active}/{page}")
//    fun getMyCaptionList(cmd: GetMyCaptionListCommand, exchange: ServerWebExchange): Mono<ApiResponse<Page<MyCaptionItem>> =
//        getMyCaptionList.handle(cmd, exchange.sessionItem))
//
//    @GetMapping("/caption/recent")
//    fun getCaptionRecent(exchange: ServerWebExchange): Mono<ApiResponse<List<CaptionRecentItem>> =
//        getCaptionRecent.handle(GetCaptionRecentCommand(page = -1)).content)
//
//    @GetMapping("/caption/recent/{page:\\d+}")
//    fun getCaptionRecent(cmd: GetCaptionRecentCommand, exchange: ServerWebExchange): Mono<ApiResponse<Page<CaptionRecentItem>> =
//        getCaptionRecent.handle(cmd))
//
//    @DeleteMapping("/caption/{animeNo}")
//    fun deleteCaption(cmd: DeleteCaptionCommand, exchange: ServerWebExchange): Mono<String> =
//        deleteCaption.handle(cmd, exchange.sessionItem)
//
//    @PostMapping("/caption/{animeNo}")
//    fun newCaption(cmd: NewCaptionCommand, exchange: ServerWebExchange): Mono<String> =
//        newCaption.handle(cmd, exchange.sessionItem)
//
//    @PutMapping("/caption/{animeNo}")
//    fun editCaption(@RequestBody cmd: EditCaptionCommand, @PathVariable animeNo: Long, exchange: ServerWebExchange): Mono<String> =
//        editCaption.handle(cmd.apply { this.animeNo = animeNo }, exchange.sessionItem)
}
