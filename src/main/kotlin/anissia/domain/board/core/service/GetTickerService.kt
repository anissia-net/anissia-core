package anissia.domain.board.core.service

import anissia.domain.board.core.model.BoardTickerItem
import anissia.domain.board.core.model.GetTickerCommand
import anissia.domain.board.core.ports.inbound.GetTicker
import anissia.domain.board.core.ports.outbound.BoardTickerRepository
import me.saro.kit.service.CacheStore
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class GetTickerService(
    private val boardTickerRepository: BoardTickerRepository,
): GetTicker {
    private val tickerCacheStore = CacheStore<String, BoardTickerItem>((24 * 60 * 60000).toLong())
    override fun handle(cmd: GetTickerCommand): BoardTickerItem {
        cmd.validate()

        return tickerCacheStore.find(cmd.ticker) {
            boardTickerRepository.findByIdOrNull(it)
                ?.let { item -> BoardTickerItem(item) }
                ?: BoardTickerItem()
        }
    }
}
