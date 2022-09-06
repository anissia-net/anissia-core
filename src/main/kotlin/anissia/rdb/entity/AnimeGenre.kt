package anissia.rdb.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table
data class AnimeGenre (
        @Id
        @Column(nullable = false, length = 100)
        var genre: String = ""
)

/*
CREATE TABLE `anime_genre` (
  `genre` varchar(100) NOT NULL,
  PRIMARY KEY (`genre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
