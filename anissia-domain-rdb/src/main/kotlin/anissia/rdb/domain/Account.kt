package anissia.rdb.domain

import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(uniqueConstraints = [
        UniqueConstraint(columnNames = ["an"]),
        UniqueConstraint(columnNames = ["email"]),
        UniqueConstraint(columnNames = ["name"]),
        // deprecated
        UniqueConstraint(columnNames = ["oldAccount"])
])
data class Account (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var an: Long = 0, // account number

        @Column(nullable = false, length = 64)
        var email: String = "",

        @Column(nullable = false, length = 512)
        var password: String = "",

        @Column(nullable = false, length = 16, unique = true)
        var name: String = "",

        @Column(nullable = false)
        var regDt: LocalDateTime = LocalDateTime.now(),

        @UpdateTimestamp
        @Column(nullable = false)
        var lastLoginDt: LocalDateTime = LocalDateTime.now(),

        @Column(nullable = true)
        var banExpireDt: LocalDateTime? = null,

        @ElementCollection
        @CollectionTable(name = "AccountRole", joinColumns = [JoinColumn(name = "an")])
        @Enumerated(EnumType.STRING)
        @Column(name = "role", nullable = false, length = 10)
        val roles: MutableSet<AccountRole> = mutableSetOf(),

        // deprecated
        @Column(nullable = false, length = 64, unique = true)
        var oldAccount: String = "",

        @Column(nullable = false)
        var oldAccountNo: Long = 0
) {
        val isBan: Boolean get() = lastLoginDt.isAfter(LocalDateTime.now())
        val isAdmin: Boolean get() = roles.any { it == AccountRole.TRANSLATOR || it == AccountRole.ROOT }
}

enum class AccountRole {
        TRANSLATOR, ROOT
}