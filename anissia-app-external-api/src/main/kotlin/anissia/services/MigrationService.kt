package anissia.services

import anissia.domain.*
import anissia.repository.*
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.stereotype.Service
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Stream
import javax.persistence.*
import javax.sql.DataSource
import kotlin.streams.toList

/**
 * 임시 : 기존 DB 마이그레이션을 위한 소스
 */
@Service
class MigrationService(
    val dataSource: DataSource,
    val accountRepository: AccountRepository,
    val animeGenreRepository: AnimeGenreRepository,
    val animeRepository: AnimeRepository,
    val boardTopicRepository: BoardTopicRepository,
    val boardPostRepository: BoardPostRepository,
    val boardTickerRepository: BoardTickerRepository,
    val captionRepository: AnimeCaptionRepository
) {
    val anMap: MutableMap<Long, Long> = mutableMapOf<Long, Long>()
    val animeMap: MutableMap<Long, Long> = mutableMapOf<Long, Long>()

    fun migration() {
        // 부모가 없는 자막 목록 삭제
        removeUnlinkedCaption()

        // 계정
        account()
        accountRepository.findAll().forEach { anMap[it.oldAccountNo] = it.an }

        // 애니메이션
        genre()
        anitime()
        animeRepository.findAll().forEach { animeMap[it.oldAnimeNo] = it.animeNo }

        // 자막
        caption()

        // 공지
        bbs()
    }

    fun removeUnlinkedCaption() = query("""
        delete from oa.anitime_cap where ai not in (
            select ai from oa.anitime union all
            select ai from oa.anitime_end
        ) or an not in (select an from oa.account)
        """.trimIndent())


    fun account() = query("""
            select * from oa.account
            where an in (select an from oa.anitime_cap group by an) or
                  (pms = '#' and an in (2, 20, 817, 942))
            order by an
        """.trimIndent()) { e ->
        Account(
            email = e.getString("mail"),
            password = "{oa}"+e.getString("password"),
            name = e.getString("name"),
            regDt = convertToLocalDateTime(e.getDate("joindate")),
            lastLoginDt = convertToLocalDateTime(e.getDate("lastdate")),
            oldAccount = e.getString("account"),
            oldAccountNo = e.getLong("an"),
            roles = if (e.getString("pms") == "#") mutableSetOf(AccountRole.ROOT) else mutableSetOf(AccountRole.TRANSLATOR)
        )
    }.also {
        accountRepository.saveAll(it)
        accountRepository.flush()
    }

    fun anitime() = query("""
        select * from (
          select week, ai, subj, time, type, src, active, startdate, enddate, 'A' as gubun from oa.anitime union all
          select week, ai, subj, time, type, src, active, startdate, enddate, 'E' as gubun from oa.anitime_end
        ) a order by (case when a.startdate != '00000000' then startdate else enddate end)
        """) { e ->
        Anime(
            status = if (e.getString("gubun") == "E") AnimeStatus.END else if (e.getString("active") == "1") AnimeStatus.ON else AnimeStatus.OFF,
            week = e.getString("week"),
            time  = e.getString("time").run { substring(0, 2) + ":" + substring(2) },
            subject = e.getString("subj"),
            genres = norGenres(e.getString("type")),
            startDate = norYmd(e.getString("startdate")),
            endDate = norYmd(e.getString("enddate")),
            website = e.getString("src").trim(),
            oldAnimeNo = e.getLong("ai")
        )
    }.also {
        animeRepository.saveAll(it)
        animeRepository.flush()
    }


    fun caption() = query("""select * from oa.anitime_cap order by ai, an""") { e ->
        AnimeCaption(
            animeNo = animeMap[e.getLong("ai")]!!,
            an = anMap[e.getLong("an")]!!,
            website = e.getString("addr1").trim() + e.getString("addr2").trim(),
            updDt = e.getString("updt").run {
                try {
                    LocalDateTime.parse(e.getString("updt"), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                } catch (e: Exception) {
                    LocalDateTime.now()
                }
            },
            episode = e.getString("sharp").run {
                when {
                    !matches("[0-9]+".toRegex()) -> "0"
                    matches("[0]+".toRegex()) -> "0"
                    this == "99990" -> "0"
                    Regex("0$").containsMatchIn(this) -> substring(0, length - 1).replace("^[0]+".toRegex(), "")
                    else -> substring(0, length - 1).replace("^[0]+".toRegex(), "") + '.' + substring(length - 1)
                }
            }.run { if (this.startsWith(".")) "0$this" else this }.run { if (this == "") "0" else this }
        )
    }.also {
        captionRepository.saveAll(it)
    }

    fun bbs() = query("select * from oa.bbs where code = 1 and (bn in (4, 537, 659) or an = 942) order by bn") { e ->
        BoardTopic(
            ticker = "notice",
            topic = e.getString("subj"),
            regDt = convertToLocalDateTime(e.getTimestamp("date")),
            an = anMap[e.getLong("an")]!!
        ).also { topic ->
            boardTopicRepository.saveAndFlush(topic)

            val content = e.getString("text")
                .replace("\r", "")
                .replace("\n", "\n\n")
                .replace("[\\n]{3,}".toRegex(), "\n\n")
                .replace("[강조]", "**")
                .replace("[/강조]", "**")

            boardPostRepository.save(BoardPost(
                topicNo = topic.topicNo,
                content = content,
                root = true,
                an = topic.an,
            ))
        }
    }.also {
        boardTickerRepository.saveAll(listOf(
            BoardTicker(ticker = "notice", name = "공지사항", writeTopic = AccountRole.ROOT, writePost = null),
            BoardTicker(ticker = "inquiry", name = "문의 게시판", writeTopic = AccountRole.ROOT, writePost = null)
        ))

    }

    fun genre() {
        animeGenreRepository.deleteAll()
        Stream.of("개그", "게임", "공포", "금융", "드라마", "레이싱", "로맨스", "마법소녀", "메르헨", "메카닉", "모험", "무협", "미소녀", "미스터리", "밀리터리", "변신", "스릴러", "스포츠", "시대물", "아동", "아이돌", "액션", "연애", "요괴", "우주", "음악", "일상", "추리", "추리물", "치유", "코미디", "판타지", "패러디", "퍼즐", "학원", "호러", "환경", "BL", "SF", "기타")
            .map { AnimeGenre(it) }
            .toList().also { animeGenreRepository.saveAll(it) }
    }

    fun <T> query(sql: String, callback: (rs: ResultSet) -> T): List<T> =
        mutableListOf<T>().apply {
            dataSource.connection.use {
                it.prepareStatement(sql).use {
                    it.executeQuery().use {
                        while (it.next()) {
                            add(callback(it))

                        }
                    }
                }
            }
        }

    fun query(sql: String): Boolean =
        dataSource.connection.use {
            it.prepareStatement(sql).use {
                it.execute()
            }
        }

    fun convertToLocalDateTime(dateToConvert: Date): LocalDateTime {
        return LocalDateTime.ofInstant(
            Date(dateToConvert.time).toInstant(), ZoneId.systemDefault()
        )
    }

    fun norYmd(ymd: String): String =
        if (ymd == "00000000" || ymd > "20990000") {
            ""
        } else ymd.run { "${substring(0, 4)}-${substring(4, 6)}-${substring(6, 8)}" }

    fun norGenres(genres: String): String =
        genres
            .split("/")
            .map { when (it.trim()) {
                "개그" -> "개그"
                "게임" -> "게임"
                "공포" -> "공포"
                "금융" -> "금융"
                "드라마" -> "드라마"
                "레이싱" -> "레이싱"
                "로맨스" -> "로맨스"
                "마법소녀" -> "마법소녀"
                "메르헨" -> "메르헨"
                "메카닉", "메카물" -> "메카닉"
                "모험", "어드벤처", "어드벤쳐" -> "모험"
                "무협" -> "무협"
                "미소녀" -> "미소녀"
                "미스터리", "미스테리" -> "미스터리"
                "밀리터리" -> "밀리터리"
                "변신" -> "변신"
                "스릴러" -> "스릴러"
                "스포츠", "운동" -> "스포츠"
                "시대물" -> "시대물"
                "아동" -> "아동"
                "아이돌" -> "아이돌"
                "액션" -> "액션"
                "연애" -> "연애"
                "요괴" -> "요괴"
                "우주" -> "우주"
                "음악" -> "음악"
                "일상" -> "일상"
                "추리" -> "추리"
                "추리물" -> "추리물"
                "치유" -> "치유"
                "코미디", "코니디" -> "코미디"
                "판타지" -> "판타지"
                "패러디" -> "패러디"
                "퍼즐" -> "퍼즐"
                "학원", "학원물" -> "학원"
                "호러" , "호러물"-> "호러"
                "환경" -> "환경"
                "BL" -> "BL"
                "SF" -> "SF"
                else -> ""
            } }
            .filter { it != "" }
            .joinToString(",")
            .run { if (this != "") this else "기타" }


}

