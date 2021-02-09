package anissia.rdb.domain

import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(name = "board_ticker_pk1", columnNames = ["ticker"])],
)
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
