package anissia.rdb.entity

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        indexes = [
                Index(name = "agenda_poll_idx__agendaNo_pollNo", columnList = "agendaNo,pollNo")
        ],
)
data class AgendaPoll (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var pollNo: Long = 0,

        @Column(nullable = false)
        var voteUp: Int = 0,

        @Column(nullable = false)
        var voteDown: Int = 0,

        @Column(nullable = false)
        var name: String = "",

        @Column(nullable = false)
        var an: Long = 0,

        @Column(nullable = true, length = 255)
        var comment: String = "",

        @Column(nullable = false)
        var regDt: LocalDateTime = LocalDateTime.now(),

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "agendaNo", foreignKey = ForeignKey(name = "agenda_poll_fk_agenda"))
        var agenda: Agenda? = null
)

/*
CREATE TABLE `agenda_poll` (
  `poll_no` bigint(20) NOT NULL AUTO_INCREMENT,
  `agenda_no` bigint(20) NOT NULL,
  `vote_up` int(11) NOT NULL,
  `vote_down` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `an` bigint(20) NOT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `reg_dt` datetime NOT NULL,
  PRIMARY KEY (`poll_no`),
  KEY `agenda_poll_idx1` (`agenda_no`,`poll_no`),
  CONSTRAINT `agenda_poll_fk1` FOREIGN KEY (`agenda_no`) REFERENCES `agenda` (`agenda_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
