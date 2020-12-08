package anissia.domain

import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [
            UniqueConstraint(columnNames = ["un"]),
            UniqueConstraint(columnNames = ["account"]),
            UniqueConstraint(columnNames = ["name"])
        ]
)
data class User (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var un: Long = 0,

        @Column(nullable = false, length = 64, unique = true)
        var account: String = "",

        @Column(nullable = false, length = 512)
        var password: String = "",

        @Column(nullable = false, length = 16, unique = true)
        var name: String = "",

        @Column(nullable = false, length = 10)
        var birth: String = "",

        @Column(nullable = false)
        var joinDt: LocalDateTime = LocalDateTime.now(),

        @UpdateTimestamp
        @Column(nullable = false)
        var lastDt: LocalDateTime = LocalDateTime.now(),

        @Column(nullable = true)
        var banExpDt: LocalDateTime? = null,

        // temp --------------------------------------------------------------------------- [start]
        @Column(nullable = false, length = 64)
        var mail: String = "",
        // temp --------------------------------------------------------------------------- [end]

        @ElementCollection
        @CollectionTable(name = "UserRole", joinColumns = [JoinColumn(name = "un")])
        @Enumerated(EnumType.STRING)
        @Column(name = "role", nullable = false, length = 100)
        val roles: MutableSet<AnissiaRole> = mutableSetOf()
) {
        val isBan: Boolean get() = banExpDt?.isAfter(LocalDateTime.now()) ?: false
}

enum class AnissiaRole() {
        ANY, ADMIN, TRANSLATOR, ROOT
}