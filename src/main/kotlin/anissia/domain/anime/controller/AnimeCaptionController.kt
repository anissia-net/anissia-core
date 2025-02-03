package anissia.domain.anime.controller


import anissia.domain.anime.command.*
import anissia.domain.anime.model.CaptionRecentItem
import anissia.domain.anime.service.CaptionService
import anissia.infrastructure.common.sessionItem
import anissia.infrastructure.common.toApiResponse
import anissia.shared.ApiResponse
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
    fun getCaptionListByAnimeNo(cmd: GetListCaptionByAnimeNoCommand, exchange: ServerWebExchange) =
        captionService.getList(cmd, exchange.sessionItem).toApiResponse

    @GetMapping("/caption/myList/{active}/{page}")
    fun getMyCaptionList(cmd: GetMyListCaptionCommand, exchange: ServerWebExchange) =
        captionService.getList(cmd, exchange.sessionItem).toApiResponse

    @GetMapping("/caption/recent")
    fun getCaptionRecent(exchange: ServerWebExchange): Mono<ApiResponse<MutableList<CaptionRecentItem>>> =
        captionService.getList(GetRecentListCaptionCommand(page = -1)).map { it.content }.toApiResponse

    @GetMapping("/caption/recent/{page:\\d+}")
    fun getCaptionRecent(cmd: GetRecentListCaptionCommand, exchange: ServerWebExchange): Mono<ApiResponse<Page<CaptionRecentItem>>> =
        captionService.getList(cmd).toApiResponse

    @DeleteMapping("/caption/{animeNo}")
    fun deleteCaption(cmd: DeleteCaptionCommand, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        captionService.delete(cmd, exchange.sessionItem).toApiResponse

    @PostMapping("/caption/{animeNo}")
    fun newCaption(cmd: AddCaptionCommand, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        captionService.add(cmd, exchange.sessionItem).toApiResponse

    @PutMapping("/caption/{animeNo}")
    fun editCaption(@RequestBody cmd: EditCaptionCommand, @PathVariable animeNo: Long, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        captionService.edit(cmd.apply { this.animeNo = animeNo }, exchange.sessionItem).toApiResponse
}
