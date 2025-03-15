package anissia.domain.store.repository

import anissia.domain.store.Store
import org.springframework.data.jpa.repository.JpaRepository

interface StoreRepository : JpaRepository<Store, String> { //, QuerydslPredicateExecutor<AnimeStore>
}
