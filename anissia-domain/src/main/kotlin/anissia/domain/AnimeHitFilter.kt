package anissia.domain

import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["an", "ip"])]
)
@IdClass(AnimeHitFilter.Key::class)
data class AnimeHitFilter (
        @Id
        @Column(nullable = false)
        var an: Long = 0,

        @Id
        @Column(nullable = false, length = 39)
        var ip: String = "",

        @Column(nullable = false, length = 10)
        var ableHitDt: LocalDateTime = LocalDateTime.now()
) {
    data class Key(var an: Long = 0, var ip: String = "") : Serializable
}
