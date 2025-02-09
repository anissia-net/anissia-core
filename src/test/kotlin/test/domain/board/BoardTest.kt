package test.domain.board

import anissia.domain.board.service.BoardService
import anissia.domain.board.service.PostService
import anissia.domain.board.service.TopicService

class BoardTest(
    private val postService: PostService,
    private val boardService: BoardService,
    private val topicService: TopicService,
) {
//    @GetMapping("/ticker/{ticker}")
//    fun getTicker(cmd: GetTickerCommand, exchange: ServerWebExchange): ResultWrapper<BoardTickerItem> =
//        ResultWrapper.ok(getTicker.handle(cmd))
//
//    @GetMapping("/topic/{ticker}/{topicNo}")
//    fun getTopic(cmd: GetTopicCommand, exchange: ServerWebExchange): ResultWrapper<BoardTopicItem> =
//        ResultWrapper.ok(getTopic.handle(cmd))
//
//    @GetMapping("/list/{ticker}/{page}")
//    fun getList(cmd: GetTopicListCommand, exchange: ServerWebExchange): ResultWrapper<Page<BoardTopicItem>> =
//        ResultWrapper.ok(getTopicList.handle(cmd))
//
//    @GetMapping("/recent/home")
//    fun getHomeRecent(exchange: ServerWebExchange): ResultWrapper<Map<String, List<Map<String, Any>>>> =
//        ResultWrapper.ok(getTopicRecentForHome.handle())
//
//    @PostMapping("/topic/{ticker}")
//    fun newTopic(@RequestBody cmd: NewTopicCommand, @PathVariable ticker: String, exchange: ServerWebExchange): ResultWrapper<Long> =
//        newTopic.handle(cmd.apply { this.ticker = ticker }, exchange.sessionItem)
//
//    @PutMapping("/topic/{topicNo}")
//    fun editTopic(@RequestBody cmd: EditTopicCommand, @PathVariable topicNo: Long, exchange: ServerWebExchange): ResultWrapper<Unit> =
//        editTopic.handle(cmd.apply { this.topicNo = topicNo }, exchange.sessionItem)
//
//    @DeleteMapping("/topic/{topicNo}")
//    fun deleteTopic(cmd: DeleteTopicCommand, exchange: ServerWebExchange): ResultWrapper<Unit> =
//        deleteTopic.handle(cmd, exchange.sessionItem)
//
//    @PostMapping("/post/{topicNo}")
//    fun newPost(@RequestBody cmd: NewPostCommand, @PathVariable topicNo: Long, exchange: ServerWebExchange): ResultWrapper<Unit> =
//        newPost.handle(cmd.apply { this.topicNo = topicNo }, exchange.sessionItem)
//
//    @PutMapping("/post/{postNo}")
//    fun editPost(@RequestBody cmd: EditPostCommand, @PathVariable postNo: Long, exchange: ServerWebExchange): ResultWrapper<Unit> =
//        editPost.handle(cmd.apply { this.postNo = postNo }, exchange.sessionItem)
//
//    @DeleteMapping("/post/{postNo}")
//    fun deletePost(cmd: DeletePostCommand, exchange: ServerWebExchange): ResultWrapper<Unit> =
//        deletePost.handle(cmd, exchange.sessionItem)
}
