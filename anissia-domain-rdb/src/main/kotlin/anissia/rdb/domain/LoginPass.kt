package anissia.rdb.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(name = "login_pass_pk1", columnNames = ["loginPassNo"])],
        indexes = [
                Index(name = "login_pass_idx1", columnList = "passDt"),
                Index(name = "login_pass_idx2", columnList = "an")
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