package anissia.rdb.repository

import anissia.rdb.domain.Asl
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AslRepository : JpaRepository<Asl, Long>, QuerydslPredicateExecutor<Asl> {
//
//    fun findAllByPubTrue(pageable: Pageable): Page<Asl>
//
//    fun findTop1ByPubTrueAndCmdAndUn(cmd: String, un: Long): Asl?
}
