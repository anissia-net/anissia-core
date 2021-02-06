package anissia.rdb.domain

import java.time.LocalDateTime
import javax.persistence.*

/**
 * asl is Anissia Log
 */
@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["agendaNo"])],
        indexes = [
                Index(columnList = "code,status,agendaNo"),
                Index(columnList = "code,status,an,agendaNo")
        ],
)
data class Agenda (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var agendaNo: Long = 0,

        @Column(nullable = false, length = 100)
        var code: String = "",

        @Column(nullable = false, length = 32)
        var status: String = "",

        @Column(nullable = false)
        var an: Long = 0,

        @Lob
        @Column(nullable = true)
        var data1: String? = null,

        @Lob
        @Column(nullable = true)
        var data2: String? = null,

        @Lob
        @Column(nullable = true)
        var data3: String? = null,

        @OneToMany(mappedBy = "agenda")
        val polls: List<AgendaPolls> = listOf(),

        @Column(nullable = false)
        var regDt: LocalDateTime = LocalDateTime.now()
)