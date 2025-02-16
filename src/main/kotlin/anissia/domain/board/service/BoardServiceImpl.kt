package anissia.domain.board.service

import anissia.domain.board.command.GetTickerCommand
import anissia.domain.board.model.BoardTickerItem
import anissia.domain.board.repository.BoardTickerRepository
import anissia.infrastructure.common.MonoCacheStore
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class BoardServiceImpl(
    private val boardTickerRepository: BoardTickerRepository,
): BoardService {

    private val tickerCacheStore = MonoCacheStore<String, BoardTickerItem>((24 * 60 * 60000).toLong())

    override fun handle(cmd: GetTickerCommand): Mono<BoardTickerItem> =
        Mono.defer {
            cmd.validate()
            tickerCacheStore.find(cmd.ticker) {
                Mono.just(boardTickerRepository.findByIdOrNull(it)
                    ?.let { item -> BoardTickerItem(item) }
                    ?: BoardTickerItem())
            }
        }
}
