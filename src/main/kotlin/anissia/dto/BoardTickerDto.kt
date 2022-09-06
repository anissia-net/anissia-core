package anissia.dto

import anissia.rdb.entity.BoardTicker

data class BoardTickerDto (
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
