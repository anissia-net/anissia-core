package anissia.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["loginPassNo"])],
        indexes = [
                Index(columnList = "passDt"),
                Index(columnList = "an")
        ]
)
data class LoginPass (
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var loginPassNo: Long = 0,

        @Column(nullable = false)
        var an: Long = 0,

        @Column(nullable = false, length = 10)
        var connType: String = "",

        @Column(nullable = false, length = 40)
        var ip: String = "",

        @Column(nullable = false)
        var passDt: LocalDateTime = LocalDateTime.now()
)