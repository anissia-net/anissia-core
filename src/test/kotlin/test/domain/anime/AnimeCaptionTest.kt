package test.domain.anime


import anissia.domain.anime.service.*

class AnimeCaptionTest(
    private val getCaptionListByAnimeNo: GetCaptionListByAnimeNo,
    private val getMyCaptionList: GetMyCaptionList,
    private val getCaptionRecent: GetCaptionRecent,
    private val newCaption: NewCaption,
    private val deleteCaption: DeleteCaption,
    private val editCaption: EditCaption
) {
//    @GetMapping("/caption/animeNo/{animeNo:\\d+}")
//    fun getCaptionListByAnimeNo(cmd: GetCaptionListByAnimeNoCommand, exchange: ServerWebExchange): ResultWrapper<List<CaptionItem>> =
//        ResultWrapper.ok(getCaptionListByAnimeNo.handle(cmd, As.toSession(exchange)))
//
//    @GetMapping("/caption/myList/{active}/{page}")
//    fun getMyCaptionList(cmd: GetMyCaptionListCommand, exchange: ServerWebExchange): ResultWrapper<Page<MyCaptionItem>> =
//        ResultWrapper.ok(getMyCaptionList.handle(cmd, As.toSession(exchange)))
//
//    @GetMapping("/caption/recent")
//    fun getCaptionRecent(exchange: ServerWebExchange): ResultWrapper<List<CaptionRecentItem>> =
//        ResultWrapper.ok(getCaptionRecent.handle(GetCaptionRecentCommand(page = -1)).content)
//
//    @GetMapping("/caption/recent/{page:\\d+}")
//    fun getCaptionRecent(cmd: GetCaptionRecentCommand, exchange: ServerWebExchange): ResultWrapper<Page<CaptionRecentItem>> =
//        ResultWrapper.ok(getCaptionRecent.handle(cmd))
//
//    @DeleteMapping("/caption/{animeNo}")
//    fun deleteCaption(cmd: DeleteCaptionCommand, exchange: ServerWebExchange): ResultWrapper<Unit> =
//        deleteCaption.handle(cmd, As.toSession(exchange))
//
//    @PostMapping("/caption/{animeNo}")
//    fun newCaption(cmd: NewCaptionCommand, exchange: ServerWebExchange): ResultWrapper<Unit> =
//        newCaption.handle(cmd, As.toSession(exchange))
//
//    @PutMapping("/caption/{animeNo}")
//    fun editCaption(@RequestBody cmd: EditCaptionCommand, @PathVariable animeNo: Long, exchange: ServerWebExchange): ResultWrapper<Unit> =
//        editCaption.handle(cmd.apply { this.animeNo = animeNo }, As.toSession(exchange))
}
