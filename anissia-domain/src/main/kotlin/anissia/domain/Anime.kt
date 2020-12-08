package anissia.domain

import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["an"])],
        indexes = [
            Index(name = "anime__idx1", columnList = "status,bcType,bcTime", unique = false),
            Index(name = "anime__idx2", columnList = "status,an", unique = false)
        ]
)
data class Anime (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var an: Long = 0,

    @Column(nullable = false)
    var status: Int = 0,

    @Column(nullable = false)
    var ongoing: Int = 0,

    @Column(nullable = false, length = 1)
    var bcType: String = "",

    @Column(nullable = false, length = 4)
    var bcTime: String = "",

    @Column(nullable = false, length = 100, unique = true)
    var subject: String = "",

    @Column(nullable = false, length = 64)
    var genres: String = "",

    @Column(nullable = false, length = 10)
    var startDate: String = "",

    @Column(nullable = false, length = 10)
    var endDate: String = "",

    @Column(nullable = false, length = 128)
    var website: String = "",

    @OneToMany(mappedBy = "anime")
    val captions: List<AnimeCaption> = listOf()
)