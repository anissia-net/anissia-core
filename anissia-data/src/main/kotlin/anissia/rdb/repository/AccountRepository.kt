package anissia.rdb.repository

import anissia.rdb.entity.Account
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AccountRepository : JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {

    @EntityGraph(attributePaths = ["roles"])
    fun findWithRolesByAn(an: Long): Account?

    @EntityGraph(attributePaths = ["roles"])
    fun findWithRolesByName(name: String): Account?

    @EntityGraph(attributePaths = ["roles"])
    fun findWithRolesByEmail(email: String): Account?

    fun findByOldAccount(oldAccount: String): Account?

    fun findByEmailAndName(email: String, name: String): Account?

    fun findByName(name: String): Account?

    fun existsByName(name: String): Boolean

    fun existsByEmail(mail: String): Boolean
}
