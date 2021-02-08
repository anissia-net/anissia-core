package anissia.rdb.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(name = "agenda_pk1", columnNames = ["agendaNo"])],
        indexes = [
                Index(name = "agenda_idx1", columnList = "code,status,agendaNo"),
                Index(name = "agenda_idx2", columnList = "code,status,an,agendaNo")
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

        @Column(nullable = false)
        var regDt: LocalDateTime = LocalDateTime.now(),

        @OneToMany(mappedBy = "agenda")
        val polls: List<AgendaPolls> = listOf()
)