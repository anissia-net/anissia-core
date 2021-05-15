package anissia.rdb.entity

import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(name = "anime_genre_pk1", columnNames = ["genre"])]
)
data class AnimeGenre (
        @Id
        @Column(nullable = false, length = 100, unique = true)
        var genre: String = ""
)

/*
CREATE TABLE `anime_genre` (
  `genre` varchar(100) NOT NULL,
  PRIMARY KEY (`genre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
