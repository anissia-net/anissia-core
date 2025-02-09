package anissia.domain.anime.controller


import anissia.domain.anime.command.*
import anissia.domain.anime.model.CaptionItem
import anissia.domain.anime.model.CaptionRecentItem
import anissia.domain.anime.model.MyCaptionItem
import anissia.domain.anime.service.CaptionService
import anissia.infrastructure.common.As
import anissia.infrastructure.common.sessionItem
import anissia.shared.ApiResponse
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/anime")
class AnimeCaptionController(
    private val captionService: CaptionService,
) {
    @GetMapping("/caption/animeNo/{animeNo:\\d+}")
    fun getCaptionListByAnimeNo(cmd: GetListCaptionByAnimeNoCommand, exchange: ServerWebExchange): Mono<ApiResponse<List<CaptionItem>>> =
        ResultWrapper.ok(captionService.getList(cmd, exchange.sessionItem))

    @GetMapping("/caption/myList/{active}/{page}")
    fun getMyCaptionList(cmd: GetMyListCaptionCommand, exchange: ServerWebExchange): Mono<ApiResponse<Page<MyCaptionItem>>> =
        ResultWrapper.ok(captionService.getList(cmd, exchange.sessionItem))

    @GetMapping("/caption/recent")
    fun getCaptionRecent(exchange: ServerWebExchange): Mono<ApiResponse<List<CaptionRecentItem>>> =
        ResultWrapper.ok(captionService.getList(GetRecentListCaptionCommand(page = -1)).content)

    @GetMapping("/caption/recent/{page:\\d+}")
    fun getCaptionRecent(cmd: GetRecentListCaptionCommand, exchange: ServerWebExchange): Mono<ApiResponse<Page<CaptionRecentItem>>> =
        ResultWrapper.ok(captionService.getList(cmd))

    @DeleteMapping("/caption/{animeNo}")
    fun deleteCaption(cmd: DeleteCaptionCommand, exchange: ServerWebExchange): Mono<ApiResponse<Unit>> =
        captionService.delete(cmd, exchange.sessionItem)

    @PostMapping("/caption/{animeNo}")
    fun newCaption(cmd: AddCaptionCommand, exchange: ServerWebExchange): Mono<ApiResponse<Unit>> =
        captionService.add(cmd, exchange.sessionItem)

    @PutMapping("/caption/{animeNo}")
    fun editCaption(@RequestBody cmd: EditCaptionCommand, @PathVariable animeNo: Long, exchange: ServerWebExchange): Mono<ApiResponse<Unit>> =
        captionService.edit(cmd.apply { this.animeNo = animeNo }, exchange.sessionItem)
}
