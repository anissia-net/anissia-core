package anissia.domain.account.core

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table
data class AccountBanName (
        @Id
        @Column(nullable = false, length = 16)
        var name: String = ""
)

/*
CREATE TABLE `account_ban_name` (
  `name` varchar(16) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
*/
