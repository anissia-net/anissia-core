package anissia.domain.board.model

class GetTickerCommand(
    val ticker: String,
) {
    fun validate() {
        require(ticker.isNotBlank()) { "ticker is blank" }
    }
}
