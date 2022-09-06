package anissia.rdb.entity

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(name = "login_token_uk__token", columnNames = ["token"])],
        indexes = [Index(name = "login_token_idx__expDt", columnList = "expDt")]
)
data class LoginToken (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var tokenNo: Long = 0,

        @Column(nullable = false, length = 512)
        var token: String = "",

        @Column(nullable = false)
        var an: Long = 0,

        @Column(nullable = false)
        var expDt: LocalDateTime = LocalDateTime.now()
) {
        val absoluteToken: String get() = "${tokenNo}-${token}"
}

/*
CREATE TABLE `login_token` (
  `token_no` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` varchar(512) NOT NULL,
  `an` bigint(20) NOT NULL,
  `exp_dt` datetime NOT NULL,
  PRIMARY KEY (`token_no`),
  UNIQUE KEY `login_token_uk1` (`token`),
  KEY `login_token_idx1` (`exp_dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
