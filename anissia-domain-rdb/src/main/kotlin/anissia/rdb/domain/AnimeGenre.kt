package anissia.rdb.domain

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
