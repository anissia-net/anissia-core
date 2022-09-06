package anissia.rdb.entity

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        indexes = [
                Index(name = "login_pass_idx__passDt", columnList = "passDt"),
                Index(name = "login_pass_idx__an", columnList = "an")
        ]
)
data class LoginPass (
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var loginPassNo: Long = 0,

        @Column(nullable = false)
        var an: Long = 0,

        @Column(nullable = false, length = 10)
        var connType: String = "",

        @Column(nullable = false, length = 40)
        var ip: String = "",

        @Column(nullable = false)
        var passDt: LocalDateTime = LocalDateTime.now()
)

/*
CREATE TABLE `login_pass` (
  `login_pass_no` bigint(20) NOT NULL AUTO_INCREMENT,
  `an` bigint(20) NOT NULL,
  `conn_type` varchar(10) NOT NULL,
  `ip` varchar(40) NOT NULL,
  `pass_dt` datetime NOT NULL,
  PRIMARY KEY (`login_pass_no`),
  KEY `login_pass_idx1` (`pass_dt`),
  KEY `login_pass_idx2` (`an`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
