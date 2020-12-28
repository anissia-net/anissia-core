package anissia.domain

import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["animeNo", "an"])],
        indexes = [Index(columnList = "an,updDt")]
)
@IdClass(AnimeCaption.Key::class)
class AnimeCaption (
        @Id
        @Column(nullable = false)
        var animeNo: Long = 0,

        @Id
        @Column(nullable = false)
        var an: Long = 0,

        @Column(nullable = false, length = 10)
        var episode: String = "0",

        @Column(nullable = false)
        var updDt: LocalDateTime = LocalDateTime.now(),

        @Column(nullable = false, length = 512)
        var website: String = "",

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "an", nullable = false, insertable = false, updatable = false)
        var account: Account? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "animeNo", nullable = false, insertable = false, updatable = false)
        var anime: Anime? = null
) {
    data class Key(val animeNo: Long = 0, val an: Long = 0) : Serializable
}
