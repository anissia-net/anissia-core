package anissia.domain.anime.core

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(
    indexes = [Index(name = "anime_hit_hour_idx__animeNo_hour", columnList = "animeNo,hour")]
)
@IdClass(AnimeHitHour.Key::class)
class AnimeHitHour (
    @Id
    @Column(nullable = false, length = 10)
    var hour: Long = 0,

    @Id
    @Column(nullable = false)
    var animeNo: Long = 0,

    @Column(nullable = false)
    var hit: Long = 0
) {
    val key get() = Key(hour, animeNo)

    class Key(var hour: Long = 0, var animeNo: Long = 0) : Serializable
}

/*
CREATE TABLE `anime_hit_hour` (
  `hour` bigint(20) NOT NULL,
  `anime_no` bigint(20) NOT NULL,
  `hit` bigint(20) NOT NULL,
  PRIMARY KEY (`anime_no`,`hour`),
  KEY `anime_hit_hour_idx1` (`anime_no`,`hour`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
