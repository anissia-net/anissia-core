package anissia.repository

import anissia.domain.AnimeStore
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AnimeStoreRepository : JpaRepository<AnimeStore, String>, QuerydslPredicateExecutor<AnimeStore>
