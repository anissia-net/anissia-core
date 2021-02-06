package anissia.rdb.domain

import java.time.LocalDateTime
import javax.persistence.*

/**
 * asl is Anissia Log
 */
@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["pollsNo"])],
        indexes = [
                Index(columnList = "agendaNo,pollsNo")
        ],
)
data class AgendaPolls (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var pollsNo: Long = 0,

        @Column(nullable = false)
        var agendaNo: Long = 0,

        @Column(nullable = false)
        var voteUp: Int = 0,

        @Column(nullable = false)
        var voteDown: Int = 0,

        @Column(nullable = false)
        var name: String = "",

        @Column(nullable = false)
        var an: Long = 0,

        @Column(nullable = true, length = 255)
        var comment: String = "",

        @OneToMany(mappedBy = "anime")
        val polls: List<AgendaPolls> = listOf(),

        @Column(nullable = false)
        var regDt: LocalDateTime = LocalDateTime.now()
)