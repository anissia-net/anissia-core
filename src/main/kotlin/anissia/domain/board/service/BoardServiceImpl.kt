package anissia.domain.board.service

import anissia.domain.board.command.GetTickerCommand
import anissia.domain.board.model.BoardTickerItem
import anissia.domain.board.repository.BoardTickerRepository
import anissia.infrastructure.common.MonoCacheStore
import anissia.shared.ApiErrorException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class BoardServiceImpl(
    private val boardTickerRepository: BoardTickerRepository,
): BoardService {
    private val tickerCacheStore = MonoCacheStore<String, BoardTickerItem>((24 * 60 * 60000).toLong())

    override fun handle(cmd: GetTickerCommand): Mono<BoardTickerItem> =
        Mono.just(cmd)
            .doOnNext { cmd.validate() }
            .flatMap { tickerCacheStore.find(cmd.ticker) { boardTickerRepository.findById(it).map { item -> BoardTickerItem(item) } } }
            .switchIfEmpty(Mono.error(ApiErrorException("존재하지 않는 게시판입니다.")))
}
