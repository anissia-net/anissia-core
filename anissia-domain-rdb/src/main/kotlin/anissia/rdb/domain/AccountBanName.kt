package anissia.rdb.domain

import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["name"])]
)
data class AccountBanName (
        @Id
        @Column(nullable = false, length = 16, unique = true)
        var name: String = ""
)
