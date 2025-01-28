package anissia.domain.board.service

import anissia.domain.board.command.GetTickerCommand
import anissia.domain.board.model.BoardTickerItem
import reactor.core.publisher.Mono

interface BoardService {
    fun handle(cmd: GetTickerCommand): Mono<BoardTickerItem>
}
