package anissia.domain.anime.controller


import anissia.domain.anime.command.*
import anissia.domain.anime.model.CaptionItem
import anissia.domain.anime.model.CaptionRecentItem
import anissia.domain.anime.model.MyCaptionItem
import anissia.domain.anime.service.CaptionService
import anissia.infrastructure.common.As
import anissia.shared.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/anime")
class AnimeCaptionController(
    private val captionService: CaptionService,
) {
    @GetMapping("/caption/animeNo/{animeNo:\\d+}")
    fun getCaptionListByAnimeNo(cmd: GetListCaptionByAnimeNoCommand, exchange: ServerWebExchange): ApiResponse<List<CaptionItem>> =
        ApiResponse.ok(captionService.getList(cmd, As.toSession(exchange)))

    @GetMapping("/caption/myList/{active}/{page}")
    fun getMyCaptionList(cmd: GetMyListCaptionCommand, exchange: ServerWebExchange): ApiResponse<Page<MyCaptionItem>> =
        ApiResponse.ok(captionService.getList(cmd, As.toSession(exchange)))

    @GetMapping("/caption/recent")
    fun getCaptionRecent(exchange: ServerWebExchange): ApiResponse<List<CaptionRecentItem>> =
        ApiResponse.ok(captionService.getList(GetRecentListCaptionCommand(page = -1)).content)

    @GetMapping("/caption/recent/{page:\\d+}")
    fun getCaptionRecent(cmd: GetRecentListCaptionCommand, exchange: ServerWebExchange): ApiResponse<Page<CaptionRecentItem>> =
        ApiResponse.ok(captionService.getList(cmd))

    @DeleteMapping("/caption/{animeNo}")
    fun deleteCaption(cmd: DeleteCaptionCommand, exchange: ServerWebExchange): ApiResponse<Void> =
        captionService.delete(cmd, As.toSession(exchange))

    @PostMapping("/caption/{animeNo}")
    fun newCaption(cmd: AddCaptionCommand, exchange: ServerWebExchange): ApiResponse<Void> =
        captionService.add(cmd, As.toSession(exchange))

    @PutMapping("/caption/{animeNo}")
    fun editCaption(@RequestBody cmd: EditCaptionCommand, @PathVariable animeNo: Long, exchange: ServerWebExchange): ApiResponse<Void> =
        captionService.edit(cmd.apply { this.animeNo = animeNo }, As.toSession(exchange))
}
