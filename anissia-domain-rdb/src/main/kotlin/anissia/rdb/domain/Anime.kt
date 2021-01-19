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
data class Anime (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var animeNo: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: AnimeStatus = AnimeStatus.ON,

    /**
     * +++ week +++
     * 0 | 日 | 일요일 | Sunday
     * 1 | 月 | 월요일 | Monday
     * 3 | 火 | 화요일 | Tuesday
     * 4 | 水 | 수요일 | Wednesday
     * 5 | 木 | 목요일 | Thursday
     * 6 | 金 | 금요일 | Friday
     * 7 | 土 | 토요일 | Saturday
     * +++ week exception +++
     * 8 | 外 | 기타 | Other
     * 9 | 新 | 신작 | New
     */
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

enum class AnimeStatus {
    ON, // on air : 방영중
    OFF, // adjournment : 휴방
    END, // ended : 완결
    DEL // delete : 삭제대기
}
