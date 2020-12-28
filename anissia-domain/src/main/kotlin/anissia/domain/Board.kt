package anissia.domain

import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["boardNo"])],
        indexes = [Index(columnList = "code,boardNo")]
)
data class Board (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var boardNo: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    var ticker: String = "",

    @Column(nullable = false, length = 64)
    var topic: String = "",

    @Lob
    @Column(nullable = false)
    var content: String = "",

    @Column(nullable = false)
    var an: Long = 0,

    @Column(nullable = false)
    var regDt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    @UpdateTimestamp
    var updDt: LocalDateTime = LocalDateTime.now(),

    @OneToOne
    @JoinColumn(name = "an", nullable = false, insertable = false, updatable = false)
    var account: Account? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "boardNo", nullable = false, insertable = false)
    var posts: MutableList<BoardPost> = mutableListOf()
)
