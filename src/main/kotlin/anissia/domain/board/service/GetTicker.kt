package anissia.domain.board.service

import anissia.domain.board.core.model.BoardTickerItem
import anissia.domain.board.core.model.GetTickerCommand

interface GetTicker {
    fun handle(cmd: GetTickerCommand): BoardTickerItem
}
