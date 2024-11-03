package anissia.domain.board.command

class GetTickerCommand(
    val ticker: String,
) {
    fun validate() {
        require(ticker.isNotBlank()) { "ticker is blank" }
    }
}
