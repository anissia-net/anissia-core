package anissia.rdb.entity

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [
            UniqueConstraint(name = "account_register_auth_uk__token", columnNames = ["token"])
        ],
        indexes = [Index(name = "account_register_auth_idx__email_expDt", columnList = "email,expDt")]
)
data class AccountRegisterAuth (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var no: Long = 0,

        @Column(nullable = false, length = 512)
        var token: String = "",

        @Column(nullable = false, length = 64)
        var email: String = "",

        @Column(nullable = false, length = 40)
        var ip: String = "",

        @Column(nullable = false)
        var data: String = "",

        @Column(nullable = false)
        var expDt: LocalDateTime = LocalDateTime.now(),

        @Column(nullable = true)
        var usedDt: LocalDateTime? = null
)

/*
CREATE TABLE `account_register_auth` (
  `no` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` varchar(512) NOT NULL,
  `email` varchar(64) NOT NULL,
  `ip` varchar(40) NOT NULL,
  `data` varchar(255) NOT NULL,
  `exp_dt` datetime NOT NULL,
  `used_dt` datetime DEFAULT NULL,
  PRIMARY KEY (`no`),
  UNIQUE KEY `account_register_auth_uk2` (`token`),
  KEY `account_register_auth_idx1` (`email`,`exp_dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
