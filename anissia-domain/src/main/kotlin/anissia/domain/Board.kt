package anissia.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["boardNo"])],
        indexes = [Index(name = "board_topic__idx", columnList = "code,boardNo")]
)
data class Board (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var boardNo: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    var code: BoardCode = BoardCode.NA,

    @Column(nullable = false, length = 64)
    var subject: String = "",

    @Lob
    @Column(nullable = false)
    var content: String = "",

    @Column(nullable = false)
    var regDt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var an: Long = 0,

    @OneToOne
    @JoinColumn(name = "an", nullable = false, insertable = false, updatable = false)
    var account: Account? = null,
)

enum class BoardCode {
    NA, NOTICE, FREE
}