package anissia.domain

import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["topicNo"])],
        indexes = [Index(columnList = "ticker,fixed,topicNo")]
)
data class BoardTopic (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var topicNo: Long = 0,

    @Column(nullable = false, length = 10)
    var ticker: String = "",

    @Column(nullable = false)
    var fixed: Boolean = false,

    @Column(nullable = false, length = 64)
    var topic: String = "",

    @Column(nullable = false)
    var an: Long = 0,

    @Column(nullable = false)
    var postCount: Int = 0,

    @Column(nullable = false)
    var regDt: LocalDateTime = LocalDateTime.now(),

    @OneToOne
    @JoinColumn(name = "an", nullable = false, insertable = false, updatable = false)
    var account: Account? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "topicNo", nullable = false, insertable = false)
    var posts: MutableList<BoardPost> = mutableListOf()
)
