package anissia.domain.anime.controller


import anissia.domain.anime.command.*
import anissia.domain.anime.model.*
import anissia.domain.anime.service.CaptionService
import anissia.infrastructure.common.As
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/anime")
class AnimeCaptionController(
    private val captionService: CaptionService,
) {
    @GetMapping("/caption/animeNo/{animeNo:\\d+}")
    fun getCaptionListByAnimeNo(cmd: GetListCaptionByAnimeNoCommand, exchange: ServerWebExchange): ResultWrapper<List<CaptionItem>> =
        ResultWrapper.ok(captionService.getList(cmd, As.toSession(exchange)))

    @GetMapping("/caption/myList/{active}/{page}")
    fun getMyCaptionList(cmd: GetMyListCaptionCommand, exchange: ServerWebExchange): ResultWrapper<Page<MyCaptionItem>> =
        ResultWrapper.ok(captionService.getList(cmd, As.toSession(exchange)))

    @GetMapping("/caption/recent")
    fun getCaptionRecent(exchange: ServerWebExchange): ResultWrapper<List<CaptionRecentItem>> =
        ResultWrapper.ok(captionService.getList(GetRecentListCaptionCommand(page = -1)).content)

    @GetMapping("/caption/recent/{page:\\d+}")
    fun getCaptionRecent(cmd: GetRecentListCaptionCommand, exchange: ServerWebExchange): ResultWrapper<Page<CaptionRecentItem>> =
        ResultWrapper.ok(captionService.getList(cmd))

    @DeleteMapping("/caption/{animeNo}")
    fun deleteCaption(cmd: DeleteCaptionCommand, exchange: ServerWebExchange): ResultWrapper<Unit> =
        captionService.delete(cmd, As.toSession(exchange))

    @PostMapping("/caption/{animeNo}")
    fun newCaption(cmd: AddCaptionCommand, exchange: ServerWebExchange): ResultWrapper<Unit> =
        captionService.add(cmd, As.toSession(exchange))

    @PutMapping("/caption/{animeNo}")
    fun editCaption(@RequestBody cmd: EditCaptionCommand, @PathVariable animeNo: Long, exchange: ServerWebExchange): ResultWrapper<Unit> =
        captionService.edit(cmd.apply { this.animeNo = animeNo }, As.toSession(exchange))
}
