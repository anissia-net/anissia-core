package anissia.domain.board.core.model

import anissia.domain.board.core.BoardTicker

class BoardTickerItem (
    var ticker: String = "",
    var name: String = "",
    var writeTopicRoles: List<String> = listOf(),
    var writePostRoles: List<String> = listOf(),
) {
    constructor(boardTicker: BoardTicker): this(
        ticker = boardTicker.ticker,
        name = boardTicker.name,
        writeTopicRoles = boardTicker.writeTopicRoles.run { split(",".toRegex()) } .filter { it != "" },
        writePostRoles = boardTicker.writePostRoles.run { split(",".toRegex()) } .filter { it != "" }
    )
}
