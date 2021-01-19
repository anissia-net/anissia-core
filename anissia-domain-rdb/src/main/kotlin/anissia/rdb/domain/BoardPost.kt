package anissia.rdb.domain

import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["postNo"])],
        indexes = [Index(columnList = "topicNo,postNo")]
)
data class BoardPost (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var postNo: Long = 0,

    @Column(nullable = false)
    var topicNo: Long = 0,

    @Column(nullable = false)
    var root: Boolean = false,

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
)
