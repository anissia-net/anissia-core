package anissia.domain.anime.repository

import anissia.domain.anime.AnimeGenre
import org.springframework.data.jpa.repository.JpaRepository

interface AnimeGenreRepository : JpaRepository<AnimeGenre, String> { //, QuerydslPredicateExecutor<AnimeGenre> {
    fun countByGenreIn(genre: List<String>): Long
}
