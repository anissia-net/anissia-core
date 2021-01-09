package anissia.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["fn"])],
        indexes = [
                Index(columnList = "failDt"),
                Index(columnList = "ip,email,failDt")
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