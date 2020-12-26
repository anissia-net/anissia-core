package anissia.domain

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["hour", "animeNo"])]
)
@IdClass(AnimeHitHour.Key::class)
data class AnimeHitHour (
        @Id
        @Column(nullable = false, length = 10)
        var hour: String = "",

        @Id
        @Column(nullable = false)
        var animeNo: Long = 0,

        @Column(nullable = false)
        var hit: Long = 0
) {
        val key get() = Key(hour, animeNo)

        data class Key(var hour: String = "", var animeNo: Long = 0) : Serializable
}
