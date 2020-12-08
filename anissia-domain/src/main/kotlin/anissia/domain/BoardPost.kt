package anissia.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["pn"])],
        indexes = [Index(name = "board_post__idx", columnList = "bn,pn")]
)
data class BoardPost (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var pn: Long = 0, // post number

        @Column(nullable = false)
        var bn: Long = 0, // board number

        @Column(nullable = false)
        var topic: Boolean = false, // is topic post

        @Column(nullable = false)
        var content: String = "",

        @Column(nullable = false)
        var regDt: LocalDateTime = LocalDateTime.now(),

        @Column(nullable = false)
        var un: Long = 0,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "un", nullable = false, insertable = false, updatable = false)
        var user: User? = null
)