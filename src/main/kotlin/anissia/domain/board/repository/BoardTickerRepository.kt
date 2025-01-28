package anissia.domain.board.repository

import anissia.domain.board.BoardTicker
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface BoardTickerRepository : ReactiveCrudRepository<BoardTicker, String>
