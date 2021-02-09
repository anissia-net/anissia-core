package anissia.rdb.domain

import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(name = "anime_hit_pk1", columnNames = ["id"])],
        indexes = [Index(name = "anime_hit_idx1", columnList = "hour, animeNo, ip")]
)
data class AnimeHit (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var id: Long = 0,

        @Column(nullable = false, length = 39)
        var ip: String = "",

        @Column(nullable = false)
        var animeNo: Long = 0,

        @Column(nullable = false)
        var hour: Long = 0
)

/*
CREATE TABLE `anime_hit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip` varchar(39) NOT NULL,
  `anime_no` bigint(20) NOT NULL,
  `hour` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `anime_hit_idx1` (`hour`,`anime_no`,`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */