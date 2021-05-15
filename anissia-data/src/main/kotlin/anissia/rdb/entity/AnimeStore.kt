package anissia.rdb.entity

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

/*
CREATE TABLE `anime_store` (
  `code` varchar(64) NOT NULL,
  `cv` varchar(128) DEFAULT NULL,
  `data` longtext DEFAULT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */