package anissia.repository

import anissia.domain.AnimeHitFilter
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface AnimeHitFilterRepository : JpaRepository<AnimeHitFilter, AnimeHitFilter.Key>, QuerydslPredicateExecutor<AnimeHitFilter> {
//
//    @Transactional
//    @Modifying
//    @Query("DELETE FROM AnimeHitFilter WHERE ableHitDt < ?1")
//    fun deleteByAbleHitDtLessThan(ableHitDt: LocalDateTime)
}