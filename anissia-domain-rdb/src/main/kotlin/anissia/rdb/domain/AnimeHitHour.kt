package anissia.rdb.domain

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["hour", "animeNo"])],
        indexes = [Index(columnList = "animeNo,hour")]
)
@IdClass(AnimeHitHour.Key::class)
data class AnimeHitHour (
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

        data class Key(var hour: Long = 0, var animeNo: Long = 0) : Serializable
}
