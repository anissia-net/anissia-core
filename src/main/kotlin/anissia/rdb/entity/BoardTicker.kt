package anissia.rdb.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table
data class BoardTicker (
    @Id
    @Column(nullable = false, length = 10)
    var ticker: String = "",

    @Column(nullable = false, length = 20)
    var name: String = "",

    @Column(nullable = false, length = 100)
    val writeTopicRoles: String = "",

    @Column(nullable = false, length = 100)
    val writePostRoles: String = ""
)

/*
CREATE TABLE `board_ticker` (
  `ticker` varchar(10) NOT NULL,
  `name` varchar(20) NOT NULL,
  `write_topic_roles` varchar(100) NOT NULL,
  `write_post_roles` varchar(100) NOT NULL,
  PRIMARY KEY (`ticker`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO anissia.board_ticker (ticker, name, write_topic_roles, write_post_roles) VALUES ('notice', '공지사항', 'ROOT,TRANSLATOR', '');
INSERT INTO anissia.board_ticker (ticker, name, write_topic_roles, write_post_roles) VALUES ('inquiry', '문의 게시판', '', '');
 */
