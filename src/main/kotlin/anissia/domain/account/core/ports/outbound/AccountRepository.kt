package anissia.domain.account.core.ports.outbound

import anissia.domain.account.core.Account
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository : JpaRepository<Account, Long> { //, QuerydslPredicateExecutor<Account> {

    @EntityGraph(attributePaths = ["roles"])
    fun findWithRolesByAn(an: Long): Account?

    @EntityGraph(attributePaths = ["roles"])
    fun findWithRolesByName(name: String): Account?

    @EntityGraph(attributePaths = ["roles"])
    fun findWithRolesByEmail(email: String): Account?

    fun findByEmailAndName(email: String, name: String): Account?

    fun findByName(name: String): Account?

    fun existsByName(name: String): Boolean

    fun existsByEmail(mail: String): Boolean
}
