package anissia.rdb.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["tokenNo"])],
        indexes = [Index(columnList = "expDt")]
)
data class LoginToken (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var tokenNo: Long = 0,

        @Column(nullable = false, length = 512, unique = true)
        var token: String = "",

        @Column(nullable = false)
        var an: Long = 0,

        @Column(nullable = false)
        var expDt: LocalDateTime = LocalDateTime.now()
) {
        val absoluteToken: String get() = "${tokenNo}-${token}"
}