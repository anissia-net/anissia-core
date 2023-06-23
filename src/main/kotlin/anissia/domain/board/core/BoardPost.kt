package anissia.domain.board.core

import anissia.domain.account.core.Account
import jakarta.persistence.*
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime

@Entity
@Table(
    indexes = [Index(name = "board_post_idx__topicNo_postNo", columnList = "topicNo,postNo")]
)
class BoardPost (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var postNo: Long = 0,

    @Column(nullable = false)
    var topicNo: Long = 0,

    @Column(nullable = false)
    var root: Boolean = false,

    @Lob
    @Column(nullable = false)
    var content: String = "",

    @Column(nullable = false)
    var an: Long = 0,

    @Column(nullable = false)
    var regDt: OffsetDateTime = OffsetDateTime.now(),

    @Column(nullable = false)
    @UpdateTimestamp
    var updDt: OffsetDateTime = OffsetDateTime.now(),

    @OneToOne
    @JoinColumn(name = "an", foreignKey = ForeignKey(name = "board_post_fk_account"), nullable = false, insertable = false, updatable = false)
    var account: Account? = null,
) {
    companion object {
        fun create(topicNo: Long, content: String, an: Long): BoardPost {
            val boardPost = BoardPost()
            boardPost.topicNo = topicNo
            boardPost.content = content
            boardPost.an = an
            return boardPost
        }

        fun createRootPost(topicNo: Long, content: String, an: Long): BoardPost {
            val boardPost = BoardPost()
            boardPost.topicNo = topicNo
            boardPost.root = true
            boardPost.content = content
            boardPost.an = an
            return boardPost
        }
    }

    fun edit(content: String) {
        this.content = content
    }
}

/*
CREATE TABLE `board_post` (
  `post_no` bigint(20) NOT NULL AUTO_INCREMENT,
  `topic_no` bigint(20) NOT NULL,
  `root` bit(1) NOT NULL,
  `content` longtext NOT NULL,
  `an` bigint(20) NOT NULL,
  `reg_dt` datetime NOT NULL,
  `upd_dt` datetime NOT NULL,
  PRIMARY KEY (`post_no`),
  KEY `board_post_idx1` (`topic_no`,`post_no`),
  KEY `board_post_fk_idx1` (`an`),
  CONSTRAINT `board_post_fk1` FOREIGN KEY (`an`) REFERENCES `account` (`an`),
  CONSTRAINT `board_post_fk2` FOREIGN KEY (`topic_no`) REFERENCES `board_topic` (`topic_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
