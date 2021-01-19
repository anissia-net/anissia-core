package anissia.rdb.domain

import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["genre"])]
)
data class AnimeGenre (
        @Id
        @Column(nullable = false, length = 100, unique = true)
        var genre: String = ""
)
