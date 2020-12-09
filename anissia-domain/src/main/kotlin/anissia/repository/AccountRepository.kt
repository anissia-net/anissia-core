package anissia.repository

import anissia.domain.Account
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AccountRepository : JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {

//    @EntityGraph(attributePaths = ["roles"])
//    fun findWithRolesByAccount(account: String): Account?
//
//    @EntityGraph(attributePaths = ["roles"])
//    fun findWithRolesByEmail(email: String): Account?
//
//    @EntityGraph(attributePaths = ["roles"])
//    fun findWithRolesByUn(un: Long): Account?
//
//    fun existsByName(name: String): Boolean
//
//    fun existsByAccount(account: String): Boolean
//
//    fun existsByEmail(mail: String): Boolean
}
