package anissia.domain.anime.repository

import anissia.domain.anime.AnimeStore
import org.springframework.data.jpa.repository.JpaRepository

interface AnimeStoreRepository : JpaRepository<AnimeStore, String> { //, QuerydslPredicateExecutor<AnimeStore>
}
