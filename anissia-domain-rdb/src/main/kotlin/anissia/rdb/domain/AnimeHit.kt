package anissia.rdb.domain

import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(name = "anime_hit_pk1", columnNames = ["id"])],
        indexes = [Index(name = "anime_hit_idx1", columnList = "hour, animeNo, ip")]
)
data class AnimeHit (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var id: Long = 0,

        @Column(nullable = false, length = 39)
        var ip: String = "",

        @Column(nullable = false)
        var animeNo: Long = 0,

        @Column(nullable = false)
        var hour: Long = 0
)
