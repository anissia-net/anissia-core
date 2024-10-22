package anissia.domain.board.model

import anissia.domain.board.BoardTicker

class BoardTickerItem (
    val ticker: String = "",
    val name: String = "",
    val writeTopicRoles: List<String> = listOf(),
    val writePostRoles: List<String> = listOf(),
    val phTopic: String = "",
) {
    constructor(boardTicker: BoardTicker): this(
        ticker = boardTicker.ticker,
        name = boardTicker.name,
        writeTopicRoles = boardTicker.writeTopicRoles.run { split(",".toRegex()) } .filter { it != "" },
        writePostRoles = boardTicker.writePostRoles.run { split(",".toRegex()) } .filter { it != "" },
        phTopic = boardTicker.phTopic
    )
}
