package anissia.rdb.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(name = "agenda_polls_pk1", columnNames = ["pollsNo"])],
        indexes = [
                Index(name = "agenda_polls_idx1", columnList = "agendaNo,pollsNo")
        ],
)
data class AgendaPolls (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var pollsNo: Long = 0,

        @Column(nullable = false)
        var agendaNo: Long = 0,

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
        @JoinColumn(name = "agendaNo", nullable = false, insertable = false, updatable = false)
        var agenda: Agenda? = null
)

/*
CREATE TABLE `agenda_polls` (
  `polls_no` bigint(20) NOT NULL AUTO_INCREMENT,
  `agenda_no` bigint(20) NOT NULL,
  `vote_up` int(11) NOT NULL,
  `vote_down` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `an` bigint(20) NOT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `reg_dt` datetime NOT NULL,
  PRIMARY KEY (`polls_no`),
  KEY `agenda_polls_idx1` (`agenda_no`,`polls_no`),
  CONSTRAINT `agenda_polls_fk1` FOREIGN KEY (`agenda_no`) REFERENCES `agenda` (`agenda_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
