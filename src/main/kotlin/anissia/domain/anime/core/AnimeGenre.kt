package anissia.domain.anime.core

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table
data class AnimeGenre (
    @Id
    @Column(nullable = false, length = 100)
    var genre: String = ""
) {

}

/*
CREATE TABLE `anime_genre` (
  `genre` varchar(100) NOT NULL,
  PRIMARY KEY (`genre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
