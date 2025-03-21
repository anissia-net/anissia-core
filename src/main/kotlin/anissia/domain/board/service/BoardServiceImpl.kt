package anissia.domain.board.service

import anissia.domain.board.command.GetTickerCommand
import anissia.domain.board.model.BoardTickerItem
import anissia.domain.board.repository.BoardTickerRepository
import me.saro.kit.service.CacheStore
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class BoardServiceImpl(
    private val boardTickerRepository: BoardTickerRepository,
): BoardService {
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
