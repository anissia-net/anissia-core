package anissia.domain

import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
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
        var createdTime: LocalDateTime = LocalDateTime.now(),

        @UpdateTimestamp
        @Column(nullable = false)
        var lastLoginTime: LocalDateTime = LocalDateTime.now(),

        @Column(nullable = true)
        var banExpireTime: LocalDateTime? = null,

        @ElementCollection
        @CollectionTable(name = "AccountRole", joinColumns = [JoinColumn(name = "an")])
        @Enumerated(EnumType.STRING)
        @Column(name = "role", nullable = false, length = 100)
        val roles: MutableSet<AccountRole> = mutableSetOf(),

        // deprecated
        @Column(nullable = false, length = 64, unique = true)
        var oldAccount: String = "",

        @Column(nullable = false)
        var oldAccountNumber: Long = 0
) {
        val isBan: Boolean get() = banExpireTime?.isAfter(LocalDateTime.now()) ?: false
}

enum class AccountRole {
        TRANSLATOR, ROOT
}