package anissia.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["pn"])],
        indexes = [
                Index(name = "user_login_pass__pass_dt", columnList = "passDt", unique = false),
                Index(name = "user_login_pass__un", columnList = "un", unique = false)
        ]
)
data class UserLoginPass (
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var pn: Long = 0,

        @Column(nullable = false)
        var un: Long = 0,

        @Column(nullable = false, length = 10)
        var connType: String = "",

        @Column(nullable = false, length = 40)
        var ip: String = "",

        @Column(nullable = false)
        var passDt: LocalDateTime = LocalDateTime.now()
)