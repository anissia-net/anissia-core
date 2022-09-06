package anissia.rdb.entity

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table
data class ActivePanel (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var apNo: Long = 0,

        @Column(nullable = false)
        var published: Boolean = false,

        @Column(nullable = false, length = 100)
        var code: String = "",

        @Column(nullable = false, length = 32)
        var status: String = "",

        @Column(nullable = false)
        var an: Long = 0,

        @Lob
        @Column(nullable = true)
        var data1: String? = null,

        @Lob
        @Column(nullable = true)
        var data2: String? = null,

        @Lob
        @Column(nullable = true)
        var data3: String? = null,

        @Column(nullable = false)
        var regDt: LocalDateTime = LocalDateTime.now()
)

/*
CREATE TABLE `active_panel` (
  `ap_no` bigint(20) NOT NULL AUTO_INCREMENT,
  `published` bit(1) NOT NULL,
  `code` varchar(100) NOT NULL,
  `status` varchar(32) NOT NULL,
  `an` bigint(20) NOT NULL,
  `data1` longtext DEFAULT NULL,
  `data2` longtext DEFAULT NULL,
  `data3` longtext DEFAULT NULL,
  `reg_dt` datetime NOT NULL,
  PRIMARY KEY (`ap_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
