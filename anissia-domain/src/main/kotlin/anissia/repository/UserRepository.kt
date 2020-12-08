package anissia.repository

import anissia.domain.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface UserRepository : JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

    @EntityGraph(attributePaths = ["roles"])
    fun findWithRolesByAccount(account: String): User?

    @EntityGraph(attributePaths = ["roles"])
    fun findWithRolesByMail(mail: String): User?

    @EntityGraph(attributePaths = ["roles"])
    fun findWithRolesByUn(un: Long): User?

    fun existsByName(name: String): Boolean

    fun existsByAccount(account: String): Boolean

    fun existsByMail(mail: String): Boolean
}
