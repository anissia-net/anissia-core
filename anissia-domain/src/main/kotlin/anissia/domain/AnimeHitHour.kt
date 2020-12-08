package anissia.domain

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["dateHour", "an"])]
)
@IdClass(AnimeHitHour.Key::class)
data class AnimeHitHour (
        @Id
        @Column(nullable = false, length = 10)
        var dateHour: String = "",

        @Id
        @Column(nullable = false)
        var an: Long = 0,

        @Column(nullable = false)
        var hit: Long = 0,

        @OneToOne
        @JoinColumn(name = "an", nullable = false, insertable = false, updatable = false)
        var anime: Anime? = null
) {
    fun addHit() {
        this.hit++
    }

    data class Key(var dateHour: String = "", var an: Long = 0) : Serializable
}
