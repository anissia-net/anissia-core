package anissia.rdb.repository

import anissia.rdb.entity.AgendaPoll
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AgendaPollRepository : JpaRepository<AgendaPoll, Long>, QuerydslPredicateExecutor<AgendaPoll> {


}
