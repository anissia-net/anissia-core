package anissia.rdb.domain

import java.time.LocalDateTime
import javax.persistence.*

/**
 * asl is Anissia Log
 */
@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["apNo"])])
data class ActivePanel (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var apNo: Long = 0,

        @Column(nullable = false)
        var published: Boolean = false,

        @Column(nullable = false, length = 100)
        var code: String = "",

        @Column(nullable = false, length = 32)
        var status: String = "",

        @Column(nullable = false)
        var an: Long = 0,

        @Column(nullable = true)
        var data1: String? = null,

        @Column(nullable = true)
        var data2: String? = null,

        @Column(nullable = true)
        var data3: String? = null,

        @Column(nullable = false)
        var regDt: LocalDateTime = LocalDateTime.now()
)