package anissia.rdb.entity

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        indexes = [
                Index(name = "login_fail_idx__failDt", columnList = "failDt"),
                Index(name = "login_fail_idx__ip_email_failDt", columnList = "ip,email,failDt")
        ]
)
data class LoginFail (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var fn: Long = 0,

        @Column(nullable = false, length = 40)
        var ip: String = "",

        @Column(nullable = false, length = 64)
        var email: String = "",

        @Column(nullable = false)
        var failDt: LocalDateTime = LocalDateTime.now()
)

/*
CREATE TABLE `login_fail` (
  `fn` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip` varchar(40) NOT NULL,
  `email` varchar(64) NOT NULL,
  `fail_dt` datetime NOT NULL,
  PRIMARY KEY (`fn`),
  KEY `login_fail_idx1` (`fail_dt`),
  KEY `login_fail_idx2` (`ip`,`email`,`fail_dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
