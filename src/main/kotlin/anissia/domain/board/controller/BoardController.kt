package anissia.domain.board.controller

import anissia.domain.board.command.*
import anissia.domain.board.model.BoardTickerItem
import anissia.domain.board.model.BoardTopicItem
import anissia.domain.board.service.BoardService
import anissia.domain.board.service.PostService
import anissia.domain.board.service.TopicService
import anissia.infrastructure.common.sessionItem
import anissia.shared.ApiResponse
import anissia.shared.ResultWrapper
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
        ResultWrapper.ok(boardService.handle(cmd))

    @GetMapping("/topic/{ticker}/{topicNo}")
    fun getTopic(cmd: GetTopicCommand, exchange: ServerWebExchange): Mono<ApiResponse<BoardTopicItem>> =
        ResultWrapper.ok(topicService.get(cmd))

    @GetMapping("/list/{ticker}/{page}")
    fun getList(cmd: GetTopicListCommand, exchange: ServerWebExchange): Mono<ApiResponse<Page<BoardTopicItem>>> =
        ResultWrapper.ok(topicService.getList(cmd))

    @GetMapping("/recent/home")
    fun getHomeRecent(exchange: ServerWebExchange): Mono<ApiResponse<Map<String, List<Map<String, Any>>>>> =
        ResultWrapper.ok(topicService.getMainRecent())

    @PostMapping("/topic/{ticker}")
    fun newTopic(@RequestBody cmd: NewTopicCommand, @PathVariable ticker: String, exchange: ServerWebExchange): Mono<ApiResponse<Long>> =
        topicService.add(cmd.apply { this.ticker = ticker }, exchange.sessionItem)

    @PutMapping("/topic/{topicNo}")
    fun editTopic(@RequestBody cmd: EditTopicCommand, @PathVariable topicNo: Long, exchange: ServerWebExchange): Mono<ApiResponse<Unit>> =
        topicService.edit(cmd.apply { this.topicNo = topicNo }, exchange.sessionItem)

    @DeleteMapping("/topic/{topicNo}")
    fun deleteTopic(cmd: DeleteTopicCommand, exchange: ServerWebExchange): Mono<ApiResponse<Unit>> =
        topicService.delete(cmd, exchange.sessionItem)

    @PostMapping("/post/{topicNo}")
    fun newPost(@RequestBody cmd: NewPostCommand, @PathVariable topicNo: Long, exchange: ServerWebExchange): Mono<ApiResponse<Unit>> =
        postService.add(cmd.apply { this.topicNo = topicNo }, exchange.sessionItem)

    @PutMapping("/post/{postNo}")
    fun editPost(@RequestBody cmd: EditPostCommand, @PathVariable postNo: Long, exchange: ServerWebExchange): Mono<ApiResponse<Unit>> =
        postService.edit(cmd.apply { this.postNo = postNo }, exchange.sessionItem)

    @DeleteMapping("/post/{postNo}")
    fun deletePost(cmd: DeletePostCommand, exchange: ServerWebExchange): Mono<ApiResponse<Unit>> =
        postService.delete(cmd, exchange.sessionItem)
}
