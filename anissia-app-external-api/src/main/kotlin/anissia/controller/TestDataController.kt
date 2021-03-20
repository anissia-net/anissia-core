package anissia.controller

import anissia.AnissiaCoreApplication
import anissia.configruration.logger
import anissia.misc.As
import anissia.rdb.domain.*
import anissia.rdb.repository.*
import anissia.services.ActivePanelService
import anissia.services.AdminService
import anissia.services.AnimeRankService
import anissia.services.AnimeService
import me.saro.kit.lang.Koreans
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

/**
 * it must use only develop server !!
 */

@RestController
@RequestMapping("/test")
class TestDataController(
    private val animeRankService: AnimeRankService,
    private val animeGenreRepository: AnimeGenreRepository,
    private val accountRepository: AccountRepository,
    private val boardTickerRepository: BoardTickerRepository,
    private val boardTopicRepository: BoardTopicRepository,
    private val animeService: AnimeService,
    private val adminService: AdminService,
    private val animeRepository: AnimeRepository,
    private val passwordEncoder: PasswordEncoder,
    private val activePanelService: ActivePanelService,
    @Value("\${env}") private val env: String
) {
    var log = logger<AnissiaCoreApplication>()

    @GetMapping("/all-anime")
    fun allAnime(): String {
        animeRepository.findAll().forEach {
            animeService.updateDocument(it)
        }
        return "OK"
    }

    // basic information
    @GetMapping("/basic", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun basic(): String {
        checkDevelopServer()

        // user
        if (accountRepository.count() > 0) {
            return """
                회원데이터가 발견되었습니다.
                이 명령어는 아무런 데이터가 없을 때만 사용할 수 있습니다.
                DB를 모두 제거한 후 실행해주세요.
            """.trimIndent();
        }

        var list = StringBuilder(1024)

        accountRepository.save(Account(email = "admin@test.com", password = passwordEncoder.encode("asdfasdf"), name = "어드민", roles = mutableSetOf(AccountRole.ROOT, AccountRole.TRANSLATOR)))
        accountRepository.save(Account(email = "user@test.com", password = passwordEncoder.encode("asdfasdf"), name = "유저"))

        // board ticker
        boardTickerRepository.save(BoardTicker("notice", "공지사항", "ROOT,TRANSLATOR", ""))
        boardTickerRepository.save(BoardTicker("inquiry", "문의 게시판", "", ""))
        list.appendLine("기본 게시판 정보 삽입")

        // anime genre
        val genres = listOf("SF","공포","드라마","로맨스","모험","무협","스포츠","액션","음악","코미디","판타지","호러")
        animeGenreRepository.saveAll(genres.map { AnimeGenre(it) })
        list.appendLine("기본 장르 삽입")

        for (i in 1..5) {
            boardTopicRepository.save(BoardTopic(ticker = "notice", topic = "공지사항 $i", an = 1))
            boardTopicRepository.save(BoardTopic(ticker = "inquiry", topic = "문의입니다 $i.", an = 2))
            activePanelService.saveText("운영게시물 $i", true, 1)
        }

        listOf("일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "외전", "기타").forEachIndexed { wi, week ->
            for (i in 1..10) {
                val subject = "$week 애니메이션 $i"
                animeRepository.save(Anime(
                    week = "$wi",
                    time = "${"%02d".format((Math.random() * 23).toInt())}:${"%02d".format((Math.random() * 59).toInt())}",
                    subject = subject,
                    autocorrect = Koreans.toJasoAtom(subject),
                    genres = genres[(genres.size * Math.random()).toInt()],
                    startDate = "",
                    endDate = "",
                    website = "",
                ))
            }
        }

        rank()

        allAnime()

        return list.appendLine("완료").toString()
    }

    // make a anime rank test data
    @GetMapping("/rank")
    fun rank(): String {
        checkDevelopServer()

        for (animeNo in 1..100) {
            if ((Math.random() * 10).toInt() != 0) {
                continue
            }
            for (hit in 0..(Math.random() * 30).toInt()) {
                animeRankService.hitAsync(animeNo.toLong(), fakeRandomIp, fakeRandomHour)
            }
        }

        animeRankService.animeRankBatch()
        return "완료"
    }

    private val fakeRandomHour get() = LocalDateTime.now().minusHours((Math.random() * 480).toLong()).format(As.DTF_RANK_HOUR).toLong()
    private val fakeRandomIp get() = "${(Math.random() * 256).toInt()}.${(Math.random() * 256).toInt()}.${(Math.random() * 256).toInt()}.${(Math.random() * 256).toInt()}"

    private fun checkDevelopServer() {
        if (env == "prod") {
            throw AccessDeniedException("this is a production server!!")
        }
    }
}