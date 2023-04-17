package anissia.domain.board.core.ports.inbound

import anissia.domain.board.core.model.BoardTickerItem
import anissia.domain.board.core.model.GetTickerCommand

interface GetTicker {
    fun handle(cmd: GetTickerCommand): BoardTickerItem
}
