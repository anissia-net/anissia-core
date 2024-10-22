package anissia.domain.board.service

import anissia.domain.board.model.BoardTickerItem
import anissia.domain.board.model.GetTickerCommand

interface GetTicker {
    fun handle(cmd: GetTickerCommand): BoardTickerItem
}
