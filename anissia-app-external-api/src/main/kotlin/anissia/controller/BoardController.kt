package anissia.controller

import anissia.dto.AnimeScheduleDto
import anissia.dto.BoardTopicDto
import anissia.services.BoardService
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/board")
class BoardController(
    private val boardService: BoardService
) {


    @GetMapping("/ticker/{ticker}")
    fun getTicker(@PathVariable ticker: String): String = boardService.getTicker(ticker)

    @GetMapping("/topic/{ticker}/{topicNo}")
    fun getTopic(@PathVariable ticker: String, @PathVariable topicNo: Long): BoardTopicDto = boardService.getTopic(ticker, topicNo)

    @GetMapping("/list/{ticker}/{page}")
    fun getList(@PathVariable ticker: String, @PathVariable page: Int): Page<BoardTopicDto> = boardService.getList(ticker, page)

    @GetMapping("/recent/home")
    fun getHomeRecent(): String = boardService.getRecent()
}