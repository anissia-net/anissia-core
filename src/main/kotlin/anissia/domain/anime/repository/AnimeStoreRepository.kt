package anissia.domain.anime.repository

import anissia.domain.anime.AnimeStore
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface AnimeStoreRepository : ReactiveCrudRepository<AnimeStore, String>
