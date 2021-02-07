package anissia.rdb.domain

import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(name = "anime_store_pk1", columnNames = ["code"])]
)
data class AnimeStore (

        @Id
        @Column(nullable = false, length = 64)
        var code: String = "", // code

        @Column(nullable = true, length = 128)
        var cv: String = "", // code value : simple value

        @Lob
        @Column(nullable = true)
        var data: String = "" // long value
)