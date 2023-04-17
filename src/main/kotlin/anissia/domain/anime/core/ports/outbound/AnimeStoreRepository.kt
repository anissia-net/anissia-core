package anissia.domain.anime.core.ports.outbound

import anissia.domain.anime.core.AnimeStore
import org.springframework.data.jpa.repository.JpaRepository

interface AnimeStoreRepository : JpaRepository<AnimeStore, String> { //, QuerydslPredicateExecutor<AnimeStore>
}
