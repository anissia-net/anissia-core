package anissia.rdb.entity

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        indexes = [Index(name = "board_topic_idx__ticker_fixed_topicNo", columnList = "ticker,fixed,topicNo")]
)
data class BoardTopic (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var topicNo: Long = 0,

    @Column(nullable = false, length = 10)
    var ticker: String = "",

    @Column(nullable = false)
    var fixed: Boolean = false,

    @Column(nullable = false, length = 64)
    var topic: String = "",

    @Column(nullable = false)
    var an: Long = 0,

    @Column(nullable = false)
    var postCount: Int = 0,

    @Column(nullable = false)
    var regDt: LocalDateTime = LocalDateTime.now(),

    @OneToOne
    @JoinColumn(name = "an", foreignKey = ForeignKey(name = "board_topic_fn_account"), nullable = false, insertable = false, updatable = false)
    var account: Account? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "topicNo", foreignKey = ForeignKey(name = "board_topic_fn_boardPost"), nullable = false, insertable = false)
    var posts: MutableList<BoardPost> = mutableListOf()
)

/*
CREATE TABLE `board_topic` (
  `topic_no` bigint(20) NOT NULL AUTO_INCREMENT,
  `ticker` varchar(10) NOT NULL,
  `fixed` bit(1) NOT NULL,
  `topic` varchar(64) NOT NULL,
  `an` bigint(20) NOT NULL,
  `post_count` int(11) NOT NULL,
  `reg_dt` datetime NOT NULL,
  PRIMARY KEY (`topic_no`),
  KEY `board_topic_idx1` (`ticker`,`fixed`,`topic_no`),
  KEY `board_topic_fk_idx1` (`an`),
  CONSTRAINT `board_topic_fk1` FOREIGN KEY (`an`) REFERENCES `account` (`an`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
