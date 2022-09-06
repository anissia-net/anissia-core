package anissia.services

import anissia.dto.ResultStatus
import anissia.misc.As
import anissia.rdb.entity.*
import anissia.rdb.repository.*
import me.saro.kit.lang.Koreans
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class DataService(
    private val animeRankService: AnimeRankService,
    private val animeGenreRepository: AnimeGenreRepository,
    private val accountRepository: AccountRepository,
    private val boardTickerRepository: BoardTickerRepository,
    private val boardTopicRepository: BoardTopicRepository,
    private val animeService: AnimeService,
    private val animeRepository: AnimeRepository,
    private val passwordEncoder: PasswordEncoder,
    private val activePanelService: ActivePanelService,
    @Value("\${env}") private val env: String
) {
    private val fakeRandomHour get() = LocalDateTime.now().minusHours((Math.random() * 800).toLong()).format(As.DTF_RANK_HOUR).toLong()
    private val fakeRandomIp get() = "${(Math.random() * 256).toInt()}.${(Math.random() * 256).toInt()}.${(Math.random() * 256).toInt()}.${(Math.random() * 256).toInt()}"

    fun updateAnimeDocument(): ResultStatus {
        animeRepository.findAll().forEach {
            animeService.updateDocument(it)
        }
        return ResultStatus("OK")
    }

    fun createAnimeHitTestData(): ResultStatus {
        checkDevelopServer()
        animeRepository.findAll().forEach forEach@{
            if ((Math.random() * 2).toInt() != 0) {
                return@forEach
            }
            for (hit in 0..(Math.random() * 30).toInt()) {
                animeRankService.hitAsync(it.animeNo, fakeRandomIp, fakeRandomHour)
            }
        }
        animeRankService.animeRankBatch()
        return ResultStatus("OK")
    }

    @Transactional
    fun createBasicTestData(): ResultStatus {
        checkDevelopServer()

        // exist user data
        if (accountRepository.count() > 0) {
            return ResultStatus("FAIL", "already exist member data, it is working on empty database")
        }

        // account - admin
        var adminAccount = accountRepository.saveAndFlush(
            Account(email = "admin@test.com", password = passwordEncoder.encode("asdfasdf"), name = "어드민", roles = mutableSetOf(
                AccountRole.ROOT, AccountRole.TRANSLATOR))
        )
        // account - user
        var userAccount = accountRepository.saveAndFlush(Account(email = "user@test.com", password = passwordEncoder.encode("asdfasdf"), name = "유저"))

        // board ticker
        boardTickerRepository.save(BoardTicker("notice", "공지사항", "ROOT,TRANSLATOR", ""))
        boardTickerRepository.save(BoardTicker("inquiry", "문의 게시판", "", ""))

        for (i in 1..5) {
            boardTopicRepository.save(BoardTopic(ticker = "notice", topic = "공지사항 $i", an = adminAccount.an))
            boardTopicRepository.save(BoardTopic(ticker = "inquiry", topic = "문의입니다 $i.", an = userAccount.an))
            activePanelService.saveText("운영기록 예제 $i", true, 1)
        }

        // anime genre
        val genres = listOf("SF","공포","드라마","로맨스","모험","무협","스포츠","액션","음악","코미디","판타지","호러")
        animeGenreRepository.saveAll(genres.map { AnimeGenre(it) })

        // anime
        listOf("일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "외전", "기타").forEachIndexed { wi, week ->
            for (i in 1..5) {
                val subject = "$week 애니메이션 $i"
                animeRepository.save(
                    Anime(
                    week = "$wi",
                    time = "${"%02d".format((Math.random() * 23).toInt())}:${"%02d".format((Math.random() * 59).toInt())}",
                    subject = subject,
                    autocorrect = Koreans.toJasoAtom(subject),
                    genres = genres[(genres.size * Math.random()).toInt()],
                    startDate = "",
                    endDate = "",
                    website = "",
                )
                )
            }
        }

        updateAnimeDocument()
        createAnimeHitTestData()

        return ResultStatus("OK")
    }

    private fun checkDevelopServer() =
        As.throwHttp400If("this is a production server!!", env == "prod")
}