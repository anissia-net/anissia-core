package anissia.domain.account.repository

import anissia.domain.account.Account
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface AccountRepository : ReactiveCrudRepository<Account, Long> {

    @EntityGraph(attributePaths = ["roles"])
    fun findWithRolesByAn(an: Long): Mono<Account>

    @EntityGraph(attributePaths = ["roles"])
    fun findWithRolesByName(name: String): Mono<Account>

    @EntityGraph(attributePaths = ["roles"])
    fun findWithRolesByEmail(email: String): Mono<Account>

    fun findByEmailAndName(email: String, name: String): Mono<Account>

    fun findByName(name: String): Mono<Account>

    fun existsByName(name: String): Mono<Boolean>

    fun existsByEmail(mail: String): Mono<Boolean>
}
