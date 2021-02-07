package anissia.rdb.domain

import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(name = "account_ban_name_pk1", columnNames = ["name"])]
)
data class AccountBanName (
        @Id
        @Column(nullable = false, length = 16, unique = true)
        var name: String = ""
)
