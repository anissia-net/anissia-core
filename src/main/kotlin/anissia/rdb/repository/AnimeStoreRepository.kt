package anissia.rdb.repository

import anissia.rdb.entity.AnimeStore
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AnimeStoreRepository : JpaRepository<AnimeStore, String>, QuerydslPredicateExecutor<AnimeStore>
