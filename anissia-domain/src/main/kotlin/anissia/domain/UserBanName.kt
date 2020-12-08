package anissia.domain

import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["name"])]
)
data class UserBanName (
        @Id
        @Column(nullable = false, length = 16, unique = true)
        var name: String = ""
)
