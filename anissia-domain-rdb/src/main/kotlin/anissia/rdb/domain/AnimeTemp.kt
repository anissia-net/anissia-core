package anissia.rdb.domain

import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
    uniqueConstraints = [UniqueConstraint(columnNames = ["animeNo"])],
    indexes = [
        Index(columnList = "status,week,time"),
        Index(columnList = "status,animeNo"),
        Index(columnList = "autocorrect"),
    ]
)
data class AnimeTemp (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var animeNo: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: AnimeStatus = AnimeStatus.ON,

    @Column(nullable = false, length = 1)
    var week: String = "",

    @Column(nullable = false, length = 5)
    var time: String = "",

    @Column(nullable = false, length = 100, unique = true)
    var subject: String = "",

    @Column(nullable = false, length = 512)
    var autocorrect: String = "",

    @Column(nullable = false, length = 64)
    var genres: String = "",

    @Column(nullable = false, length = 10)
    var startDate: String = "",

    @Column(nullable = false, length = 10)
    var endDate: String = "",

    @Column(nullable = false, length = 128)
    var website: String = "",

    @UpdateTimestamp
    @Column(nullable = false)
    var updDt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "anime")
    val captions: List<AnimeCaption> = listOf(),

    @Column(nullable = false)
    var oldAnimeNo: Long = 0
)
