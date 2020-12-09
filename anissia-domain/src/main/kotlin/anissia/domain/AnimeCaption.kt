package anissia.domain

import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["an", "un"])],
        indexes = [Index(name = "anime_caption__idx", columnList = "un,updDt")]
)
@IdClass(AnimeCaption.Key::class)
data class AnimeCaption (
        @Id
        @Column(nullable = false)
        var an: Long = 0,

        @Id
        @Column(nullable = false)
        var un: Long = 0,

        @Column(nullable = false, length = 10)
        var episode: String = "0",

        @Column(nullable = false, length = 14)
        var updDt: LocalDateTime = LocalDateTime.now(),

        @Column(nullable = false, length = 256)
        var website: String = "",

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "un", nullable = false, insertable = false, updatable = false)
        var account: Account? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "an", nullable = false, insertable = false, updatable = false)
        var anime: Anime? = null
) {
    data class Key(val an: Long = 0, val un: Long = 0) : Serializable
}
