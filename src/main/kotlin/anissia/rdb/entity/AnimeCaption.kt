package anissia.rdb.entity

import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        indexes = [
                Index(name = "anime_caption_idx__an_updDt", columnList = "an,updDt"),
                Index(name = "anime_caption_idx__updDt", columnList = "updDt")
        ]
)
@IdClass(AnimeCaption.Key::class)
data class AnimeCaption (
        @Id
        @Column(nullable = false)
        var an: Long = 0,

        @Column(nullable = false, length = 10)
        var episode: String = "0",

        @Column(nullable = false)
        var updDt: LocalDateTime = LocalDateTime.now(),

        @Column(nullable = false, length = 512)
        var website: String = "",

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "an", foreignKey = ForeignKey(name = "anime_caption_fk_account"), nullable = false, insertable = false, updatable = false)
        var account: Account? = null,

        @Id
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "animeNo", foreignKey = ForeignKey(name = "anime_caption_fk_anime"))
        var anime: Anime? = null
) {
    data class Key(val anime: Long = 0, val an: Long = 0) : Serializable
}

/*
CREATE TABLE `anime_caption` (
  `anime_no` bigint(20) NOT NULL,
  `an` bigint(20) NOT NULL,
  `episode` varchar(10) NOT NULL,
  `upd_dt` datetime NOT NULL,
  `website` varchar(512) NOT NULL,
  PRIMARY KEY (`an`,`anime_no`),
  KEY `anime_caption_idx1` (`an`,`upd_dt`),
  KEY `anime_caption_idx2` (`upd_dt`),
  KEY `anime_caption_fk_idx1` (`anime_no`),
  CONSTRAINT `anime_caption_fk1` FOREIGN KEY (`anime_no`) REFERENCES `anime` (`anime_no`),
  CONSTRAINT `anime_caption_fk2` FOREIGN KEY (`an`) REFERENCES `account` (`an`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
