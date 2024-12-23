package anissia.domain.board

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table
class BoardTicker (
    @Id
    @Column(nullable = false, length = 10)
    var ticker: String = "",

    @Column(nullable = false, length = 20)
    var name: String = "",

    @Column(nullable = false, length = 100)
    val writeTopicRoles: String = "",

    @Column(nullable = false, length = 100)
    val writePostRoles: String = "",

    @Column(nullable = false, length = 1024)
    val phTopic: String = ""
) {

}

/*
CREATE TABLE `board_ticker` (
  `ticker` varchar(10) NOT NULL,
  `name` varchar(20) NOT NULL,
  `write_topic_roles` varchar(100) NOT NULL,
  `write_post_roles` varchar(100) NOT NULL,
  `ph_topic` varchar(1024) NOT NULL,
  PRIMARY KEY (`ticker`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
