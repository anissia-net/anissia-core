package anissia.domain.board.repository

import anissia.domain.board.BoardTicker
import org.springframework.data.jpa.repository.JpaRepository

interface BoardTickerRepository : JpaRepository<BoardTicker, String>
