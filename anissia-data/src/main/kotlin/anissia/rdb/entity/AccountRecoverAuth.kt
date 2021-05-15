package anissia.rdb.entity

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(name = "account_recover_auth_pk1", columnNames = ["no"])],
        indexes = [Index(name = "account_recover_auth_idx1", columnList = "an,expDt")]
)
data class AccountRecoverAuth (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var no: Long = 0,

        @Column(nullable = false, length = 512, unique = true)
        var token: String = "",

        @Column(nullable = false)
        var an: Long = 0,

        @Column(nullable = false, length = 40)
        var ip: String = "",

        @Column(nullable = false)
        var expDt: LocalDateTime = LocalDateTime.now(),

        @Column(nullable = true)
        var usedDt: LocalDateTime? = null,

        @OneToOne
        @JoinColumn(name = "an", nullable = false, insertable = false, updatable = false)
        var account: Account? = null
)

/*
CREATE TABLE `account_recover_auth` (
  `no` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` varchar(512) NOT NULL,
  `an` bigint(20) NOT NULL,
  `ip` varchar(40) NOT NULL,
  `exp_dt` datetime NOT NULL,
  `used_dt` datetime DEFAULT NULL,
  PRIMARY KEY (`no`),
  UNIQUE KEY `account_recover_auth_uk1` (`token`),
  KEY `account_recover_auth_idx1` (`an`,`exp_dt`),
  CONSTRAINT `account_recover_auth_fk1` FOREIGN KEY (`an`) REFERENCES `account` (`an`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
*/