package anissia.controller

import anissia.AnissiaCoreApplication
import anissia.configruration.logger
import anissia.misc.As
import anissia.rdb.domain.AccountRole
import anissia.rdb.domain.AnimeGenre
import anissia.rdb.domain.BoardTicker
import anissia.rdb.repository.AccountRepository
import anissia.rdb.repository.AnimeGenreRepository
import anissia.rdb.repository.AnimeRepository
import anissia.rdb.repository.BoardTickerRepository
import anissia.services.AnimeRankService
import anissia.services.AnimeService
import io.netty.handler.codec.http.HttpContent
import org.apache.http.entity.ContentType
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
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
    private val animeService: AnimeService,
    private val animeRepository: AnimeRepository,
    @Value("\${env}") private val env: String
) {
    var log = logger<AnissiaCoreApplication>()

    @GetMapping("/all-anime")
    fun allAnime() {
        animeRepository.findAll().forEach {
            animeService.updateDocument(it)
        }
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

        //INSERT INTO anissia.account (an, ban_expire_dt, email, last_login_dt, name, old_account, old_account_no, password, reg_dt) VALUES (1, null, 'j@saro.me', '2021-02-09 09:00:56', '박용서', 'saro', 2, '{bcrypt}$2a$10$mRmuhIlTgx2/54SkMpEJb..nNiwC1CABakh5pNx8.xVFeEMzrOIJG', '2010-08-18 00:00:00');

        // board ticker
        boardTickerRepository.save(BoardTicker("notice", "공지사항", "ROOT,TRANSLATOR", ""))
        boardTickerRepository.save(BoardTicker("inquiry", "문의 게시판", "", ""))
        list.appendLine("기본 게시판 정보 삽입")

        // anime genre
        animeGenreRepository.saveAll(listOf("SF","공포","드라마","로맨스","모험","무협","스포츠","액션","음악","코미디","판타지","호러").map { AnimeGenre(it) })
        list.appendLine("기본 장르 삽입")

        return list.appendLine("완료").toString()
    }

    // make a anime rank test data
    @GetMapping("/rank")
    fun rank(): String {
        checkDevelopServer()

        for (animeNo in 1..2000) {
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