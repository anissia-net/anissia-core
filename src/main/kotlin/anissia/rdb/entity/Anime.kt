package anissia.rdb.entity

import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(name = "anime_uk__subject", columnNames = ["subject"])
    ],
    indexes = [
        Index(name = "anime_idx__status_week_time", columnList = "status,week,time"),
        Index(name = "anime_idx__status_animeNo", columnList = "status,animeNo"),
        Index(name = "anime_idx__autocorrect", columnList = "autocorrect"),
    ],
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

    @Column(nullable = false, length = 100)
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

    @Column(nullable = false)
    var captionCount: Int = 0,

    @UpdateTimestamp
    @Column(nullable = false)
    var updDt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "anime")
    val captions: List<AnimeCaption> = listOf()
)

/*
CREATE TABLE `anime` (
  `anime_no` bigint(20) NOT NULL AUTO_INCREMENT,
  `status` varchar(255) NOT NULL,
  `week` varchar(1) NOT NULL,
  `time` varchar(5) NOT NULL,
  `subject` varchar(100) NOT NULL,
  `autocorrect` varchar(512) NOT NULL,
  `genres` varchar(64) NOT NULL,
  `start_date` varchar(10) NOT NULL,
  `end_date` varchar(10) NOT NULL,
  `website` varchar(128) NOT NULL,
  `caption_count` int(11) NOT NULL,
  `upd_dt` datetime NOT NULL,
  PRIMARY KEY (`anime_no`),
  UNIQUE KEY `anime_uk1` (`subject`),
  KEY `anime_idx1` (`status`,`week`,`time`),
  KEY `anime_idx2` (`status`,`anime_no`),
  KEY `anime_idx3` (`autocorrect`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
