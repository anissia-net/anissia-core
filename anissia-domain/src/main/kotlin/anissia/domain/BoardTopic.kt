package anissia.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["bn"])],
        indexes = [Index(name = "board_topic__idx", columnList = "code,bn")]
)
data class BoardTopic (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var bn: Long = 0,

        @Column(nullable = false, length = 10)
        var code: String = "",

        @Column(nullable = false, length = 64)
        var subject: String = "",

        @Column(nullable = false)
        var regDt: LocalDateTime = LocalDateTime.now(),

        @Column(nullable = false)
        var postCount: Int = 0,

        @Column(nullable = false)
        var un: Long = 0,

        @OneToOne
        @JoinColumn(name = "un", nullable = false, insertable = false, updatable = false)
        var user: User? = null,

        @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
        @JoinColumn(name = "bn", nullable = false, insertable = false)
        var posts: MutableList<BoardPost> = mutableListOf()
)
