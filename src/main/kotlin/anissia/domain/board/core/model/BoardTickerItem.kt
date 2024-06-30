package anissia.domain.board.core.model

import anissia.domain.board.core.BoardTicker

class BoardTickerItem (
    val ticker: String = "",
    val name: String = "",
    val writeTopicRoles: List<String> = listOf(),
    val writePostRoles: List<String> = listOf(),
    val placeholder: String = "내용",
) {
    constructor(boardTicker: BoardTicker): this(
        ticker = boardTicker.ticker,
        name = boardTicker.name,
        writeTopicRoles = boardTicker.writeTopicRoles.run { split(",".toRegex()) } .filter { it != "" },
        writePostRoles = boardTicker.writePostRoles.run { split(",".toRegex()) } .filter { it != "" },
        placeholder = boardTicker.placeholder
    )
}
