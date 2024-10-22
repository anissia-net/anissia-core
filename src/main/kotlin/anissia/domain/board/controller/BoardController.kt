package anissia.domain.board.controller

import anissia.domain.board.core.model.*
import anissia.domain.board.core.service.*
import anissia.infrastructure.common.As
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/board")
class BoardController(
    private val deletePost: DeletePost,
    private val deleteTopic: anissia.domain.board.service.DeleteTopic,
    private val editPost: EditPost,
    private val editTopic: EditTopic,
    private val getTicker: GetTicker,
    private val getTopicList: anissia.domain.board.service.GetTopicList,
    private val getTopicRecentForHome: GetTopicRecentForHome,
    private val getTopic: GetTopic,
    private val newPost: NewPost,
    private val newTopic: NewTopic,
) {
    @GetMapping("/ticker/{ticker}")
    fun getTicker(cmd: GetTickerCommand, exchange: ServerWebExchange): ResultWrapper<BoardTickerItem> =
        ResultWrapper.ok(getTicker.handle(cmd))

    @GetMapping("/topic/{ticker}/{topicNo}")
    fun getTopic(cmd: GetTopicCommand, exchange: ServerWebExchange): ResultWrapper<BoardTopicItem> =
        ResultWrapper.ok(getTopic.handle(cmd))

    @GetMapping("/list/{ticker}/{page}")
    fun getList(cmd: GetTopicListCommand, exchange: ServerWebExchange): ResultWrapper<Page<BoardTopicItem>> =
        ResultWrapper.ok(getTopicList.handle(cmd))

    @GetMapping("/recent/home")
    fun getHomeRecent(exchange: ServerWebExchange): ResultWrapper<Map<String, List<Map<String, Any>>>> =
        ResultWrapper.ok(getTopicRecentForHome.handle())

    @PostMapping("/topic/{ticker}")
    fun newTopic(@RequestBody cmd: NewTopicCommand, @PathVariable ticker: String, exchange: ServerWebExchange): ResultWrapper<Long> =
        newTopic.handle(cmd.apply { this.ticker = ticker }, As.toSession(exchange))

    @PutMapping("/topic/{topicNo}")
    fun editTopic(@RequestBody cmd: EditTopicCommand, @PathVariable topicNo: Long, exchange: ServerWebExchange): ResultWrapper<Unit> =
        editTopic.handle(cmd.apply { this.topicNo = topicNo }, As.toSession(exchange))

    @DeleteMapping("/topic/{topicNo}")
    fun deleteTopic(cmd: DeleteTopicCommand, exchange: ServerWebExchange): ResultWrapper<Unit> =
        deleteTopic.handle(cmd, As.toSession(exchange))

    @PostMapping("/post/{topicNo}")
    fun newPost(@RequestBody cmd: NewPostCommand, @PathVariable topicNo: Long, exchange: ServerWebExchange): ResultWrapper<Unit> =
        newPost.handle(cmd.apply { this.topicNo = topicNo }, As.toSession(exchange))

    @PutMapping("/post/{postNo}")
    fun editPost(@RequestBody cmd: EditPostCommand, @PathVariable postNo: Long, exchange: ServerWebExchange): ResultWrapper<Unit> =
        editPost.handle(cmd.apply { this.postNo = postNo }, As.toSession(exchange))

    @DeleteMapping("/post/{postNo}")
    fun deletePost(cmd: DeletePostCommand, exchange: ServerWebExchange): ResultWrapper<Unit> =
        deletePost.handle(cmd, As.toSession(exchange))
}
