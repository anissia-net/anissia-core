package anissia.rdb.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(name = "login_fail_pk1", columnNames = ["fn"])],
        indexes = [
                Index(name = "login_fail_idx1", columnList = "failDt"),
                Index(name = "login_fail_idx2", columnList = "ip,email,failDt")
        ]
)
data class LoginFail (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var fn: Long = 0,

        @Column(nullable = false, length = 40)
        var ip: String = "",

        @Column(nullable = false, length = 64)
        var email: String = "",

        @Column(nullable = false)
        var failDt: LocalDateTime = LocalDateTime.now()
)