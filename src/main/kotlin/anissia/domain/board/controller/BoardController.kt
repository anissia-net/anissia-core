package anissia.domain.board.controller

import anissia.domain.board.command.*
import anissia.domain.board.model.BoardTickerItem
import anissia.domain.board.model.BoardTopicItem
import anissia.domain.board.service.BoardService
import anissia.domain.board.service.PostService
import anissia.domain.board.service.TopicService
import anissia.infrastructure.common.sessionItem
import anissia.infrastructure.common.toApiResponse
import anissia.shared.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/board")
class BoardController(
    private val boardService: BoardService,
    private val topicService: TopicService,
    private val postService: PostService,
) {
    @GetMapping("/ticker/{ticker}")
    fun getTicker(cmd: GetTickerCommand, exchange: ServerWebExchange): Mono<ApiResponse<BoardTickerItem>> =
        boardService.handle(cmd).toApiResponse

    @GetMapping("/topic/{ticker}/{topicNo}")
    fun getTopic(cmd: GetTopicCommand, exchange: ServerWebExchange): Mono<ApiResponse<BoardTopicItem>> =
        topicService.get(cmd).toApiResponse

    @GetMapping("/list/{ticker}/{page}")
    fun getList(cmd: GetTopicListCommand, exchange: ServerWebExchange): Mono<ApiResponse<Page<BoardTopicItem>>> =
        topicService.getList(cmd).toApiResponse

    @GetMapping("/recent/home")
    fun getHomeRecent(exchange: ServerWebExchange): Mono<ApiResponse<Map<String, List<Map<String, Any>>>>> =
        topicService.getMainRecent().toApiResponse

    @PostMapping("/topic/{ticker}")
    fun newTopic(@RequestBody cmd: NewTopicCommand, @PathVariable ticker: String, exchange: ServerWebExchange): Mono<ApiResponse<Long>> =
        topicService.add(cmd.apply { this.ticker = ticker }, exchange.sessionItem).toApiResponse

    @PutMapping("/topic/{topicNo}")
    fun editTopic(@RequestBody cmd: EditTopicCommand, @PathVariable topicNo: Long, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        topicService.edit(cmd.apply { this.topicNo = topicNo }, exchange.sessionItem).toApiResponse

    @DeleteMapping("/topic/{topicNo}")
    fun deleteTopic(cmd: DeleteTopicCommand, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        topicService.delete(cmd, exchange.sessionItem).toApiResponse

    @PostMapping("/post/{topicNo}")
    fun newPost(@RequestBody cmd: NewPostCommand, @PathVariable topicNo: Long, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        postService.add(cmd.apply { this.topicNo = topicNo }, exchange.sessionItem).toApiResponse

    @PutMapping("/post/{postNo}")
    fun editPost(@RequestBody cmd: EditPostCommand, @PathVariable postNo: Long, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        postService.edit(cmd.apply { this.postNo = postNo }, exchange.sessionItem).toApiResponse

    @DeleteMapping("/post/{postNo}")
    fun deletePost(cmd: DeletePostCommand, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        postService.delete(cmd, exchange.sessionItem).toApiResponse
}
