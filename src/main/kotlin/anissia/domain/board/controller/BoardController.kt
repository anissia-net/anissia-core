package anissia.domain.board.controller

import anissia.domain.board.command.*
import anissia.domain.board.model.BoardTickerItem
import anissia.domain.board.model.BoardTopicItem
import anissia.domain.board.service.BoardService
import anissia.domain.board.service.PostService
import anissia.domain.board.service.TopicService
import anissia.infrastructure.common.As
import anissia.shared.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/board")
class BoardController(
    private val boardService: BoardService,
    private val topicService: TopicService,
    private val postService: PostService,
) {
    @GetMapping("/ticker/{ticker}")
    fun getTicker(cmd: GetTickerCommand, exchange: ServerWebExchange): ApiResponse<BoardTickerItem> =
        ApiResponse.ok(boardService.handle(cmd))

    @GetMapping("/topic/{ticker}/{topicNo}")
    fun getTopic(cmd: GetTopicCommand, exchange: ServerWebExchange): ApiResponse<BoardTopicItem> =
        ApiResponse.ok(topicService.get(cmd))

    @GetMapping("/list/{ticker}/{page}")
    fun getList(cmd: GetTopicListCommand, exchange: ServerWebExchange): ApiResponse<Page<BoardTopicItem>> =
        ApiResponse.ok(topicService.getList(cmd))

    @GetMapping("/recent/home")
    fun getHomeRecent(exchange: ServerWebExchange): ApiResponse<Map<String, List<Map<String, Any>>>> =
        ApiResponse.ok(topicService.getMainRecent())

    @PostMapping("/topic/{ticker}")
    fun newTopic(@RequestBody cmd: NewTopicCommand, @PathVariable ticker: String, exchange: ServerWebExchange): ApiResponse<Long> =
        topicService.add(cmd.apply { this.ticker = ticker }, As.toSession(exchange))

    @PutMapping("/topic/{topicNo}")
    fun editTopic(@RequestBody cmd: EditTopicCommand, @PathVariable topicNo: Long, exchange: ServerWebExchange): ApiResponse<Unit> =
        topicService.edit(cmd.apply { this.topicNo = topicNo }, As.toSession(exchange))

    @DeleteMapping("/topic/{topicNo}")
    fun deleteTopic(cmd: DeleteTopicCommand, exchange: ServerWebExchange): ApiResponse<Unit> =
        topicService.delete(cmd, As.toSession(exchange))

    @PostMapping("/post/{topicNo}")
    fun newPost(@RequestBody cmd: NewPostCommand, @PathVariable topicNo: Long, exchange: ServerWebExchange): ApiResponse<Unit> =
        postService.add(cmd.apply { this.topicNo = topicNo }, As.toSession(exchange))

    @PutMapping("/post/{postNo}")
    fun editPost(@RequestBody cmd: EditPostCommand, @PathVariable postNo: Long, exchange: ServerWebExchange): ApiResponse<Unit> =
        postService.edit(cmd.apply { this.postNo = postNo }, As.toSession(exchange))

    @DeleteMapping("/post/{postNo}")
    fun deletePost(cmd: DeletePostCommand, exchange: ServerWebExchange): ApiResponse<Unit> =
        postService.delete(cmd, As.toSession(exchange))
}
