package anissia.domain.board.controller

import anissia.domain.board.command.*
import anissia.domain.board.model.*
import anissia.domain.board.service.*
import anissia.infrastructure.common.As
import anissia.shared.ResultWrapper
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
    fun getTicker(cmd: GetTickerCommand, exchange: ServerWebExchange): ResultWrapper<BoardTickerItem> =
        ResultWrapper.ok(boardService.handle(cmd))

    @GetMapping("/topic/{ticker}/{topicNo}")
    fun getTopic(cmd: GetTopicCommand, exchange: ServerWebExchange): ResultWrapper<BoardTopicItem> =
        ResultWrapper.ok(topicService.get(cmd))

    @GetMapping("/list/{ticker}/{page}")
    fun getList(cmd: GetTopicListCommand, exchange: ServerWebExchange): ResultWrapper<Page<BoardTopicItem>> =
        ResultWrapper.ok(topicService.getList(cmd))

    @GetMapping("/recent/home")
    fun getHomeRecent(exchange: ServerWebExchange): ResultWrapper<Map<String, List<Map<String, Any>>>> =
        ResultWrapper.ok(topicService.getMainRecent())

    @PostMapping("/topic/{ticker}")
    fun newTopic(@RequestBody cmd: NewTopicCommand, @PathVariable ticker: String, exchange: ServerWebExchange): ResultWrapper<Long> =
        topicService.add(cmd.apply { this.ticker = ticker }, As.toSession(exchange))

    @PutMapping("/topic/{topicNo}")
    fun editTopic(@RequestBody cmd: EditTopicCommand, @PathVariable topicNo: Long, exchange: ServerWebExchange): ResultWrapper<Unit> =
        topicService.edit(cmd.apply { this.topicNo = topicNo }, As.toSession(exchange))

    @DeleteMapping("/topic/{topicNo}")
    fun deleteTopic(cmd: DeleteTopicCommand, exchange: ServerWebExchange): ResultWrapper<Unit> =
        topicService.delete(cmd, As.toSession(exchange))

    @PostMapping("/post/{topicNo}")
    fun newPost(@RequestBody cmd: NewPostCommand, @PathVariable topicNo: Long, exchange: ServerWebExchange): ResultWrapper<Unit> =
        postService.add(cmd.apply { this.topicNo = topicNo }, As.toSession(exchange))

    @PutMapping("/post/{postNo}")
    fun editPost(@RequestBody cmd: EditPostCommand, @PathVariable postNo: Long, exchange: ServerWebExchange): ResultWrapper<Unit> =
        postService.edit(cmd.apply { this.postNo = postNo }, As.toSession(exchange))

    @DeleteMapping("/post/{postNo}")
    fun deletePost(cmd: DeletePostCommand, exchange: ServerWebExchange): ResultWrapper<Unit> =
        postService.delete(cmd, As.toSession(exchange))
}
