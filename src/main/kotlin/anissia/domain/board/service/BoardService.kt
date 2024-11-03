package anissia.domain.board.service

import anissia.domain.board.model.BoardTickerItem
import anissia.domain.board.command.GetTickerCommand

interface BoardService {
    fun handle(cmd: GetTickerCommand): BoardTickerItem
}
