package anissia.domain.board.service

import anissia.domain.board.command.GetTickerCommand
import anissia.domain.board.model.BoardTickerItem

interface BoardService {
    fun handle(cmd: GetTickerCommand): BoardTickerItem
}
