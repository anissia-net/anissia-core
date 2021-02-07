package anissia.rdb.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(name = "account_register_auth_pk1", columnNames = ["no"])],
        indexes = [Index(name = "account_register_auth_idx1", columnList = "email,expDt")]
)
data class AccountRegisterAuth (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var no: Long = 0,

        @Column(nullable = false, length = 512, unique = true)
        var token: String = "",

        @Column(nullable = false, length = 64, unique = true)
        var email: String = "",

        @Column(nullable = false, length = 40)
        var ip: String = "",

        @Column(nullable = false)
        var data: String = "",

        @Column(nullable = false)
        var expDt: LocalDateTime = LocalDateTime.now(),

        @Column(nullable = true)
        var usedDt: LocalDateTime? = null
)
