package anissia.services

import anissia.domain.*
import anissia.repository.AccountRepository
import anissia.repository.AnimeGenreRepository
import org.springframework.stereotype.Service
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.stream.Stream
import javax.sql.DataSource
import kotlin.streams.toList

@Service
class MigrationService(
    val dataSource: DataSource,
    val accountRepository: AccountRepository,
    val animeGenreRepository: AnimeGenreRepository
) {
    val anMap = mutableMapOf<Long, Long>()
    val animeMap = mutableMapOf<Long, Long>()

    fun migration() {
        // 부모가 없는 자막 목록 삭제
//        removeUnlinkedCaption()

        // 계정
        //account()
        //accountRepository.findAll().forEach { anMap[it.oldAccountNumber] = it.an }

//        // 애니메이션
//        genre()
        anitime()
//
//        // 자막
//        caption()
//
//        // 공지
//        bbs()
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
            password = e.getString("password"),
            name = e.getString("name"),
            createdTime = convertToLocalDateTime(e.getDate("joindate")),
            lastLoginTime = convertToLocalDateTime(e.getDate("lastdate")),
            oldAccount = e.getString("account"),
            oldAccountNo = e.getLong("an"),
            roles = if (e.getString("pms") == "#") mutableSetOf(AccountRole.ROOT) else mutableSetOf(AccountRole.TRANSLATOR)
        )
    }.also {
        accountRepository.saveAll(it)
        accountRepository.flush()
    }

    fun anitime() =
        query("""
            select * from (
              select ai, subj, time, type, src, active, startdate, enddate, 'A' as gubun from oa.anitime union all
              select ai, subj, time, type, src, active, startdate, enddate, 'E' as gubun from oa.anitime_end
            ) a order by (case when a.startdate != '00000000' then startdate else enddate end)
            """) { e ->
            Anime(
                status = if (e.getString("gubun") == "E") AnimeStatus.END else if (e.getString("active") == "1") AnimeStatus.ON else AnimeStatus.OFF,
                cycle = e.getString("week"),
                time  = e.getString("time"),
                subject = e.getString("subj"),
                genres = norGenres(e.getString("type")),
                startDate = norYmd(e.getString("startdate")),
                endDate = norYmd(e.getString("enddate")),
                website = e.getString("src"),
                oldAnimeNo = e.getLong("ai")
            )
        }.also {
            println(it)
        }


    fun caption() {
        query("""select * from oa.anitime_cap order by ai, an""") { e ->
            val animeNo = e.getLong("ai")
            val an = e.getLong("an")
            val sharp = e.getString("sharp")
            val updt = e.getString("updt")
            val addr1 = e.getString("addr1")
            val addr2 = e.getString("addr2")

            println("$animeNo $an $sharp $updt $addr1 $addr2")
        }
    }

    fun bbs() {
        query("select * from oa.bbs where code = 1 and (bn in (4, 537, 659) or an = 942) order by bn") { e ->
            val boardNo = e.getLong("bn")
            val an = e.getLong("an")
            val subject = e.getString("subj")
            val text = e.getString("text")
            val date = e.getDate("date").toLocalDate()

            println("$boardNo $an $subject $text $date")
        }
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

    fun query(sql: String) =
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

    fun norGenres(genres: String) =
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

