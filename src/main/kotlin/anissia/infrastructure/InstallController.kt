package anissia.infrastructure

import anissia.domain.account.core.ports.outbound.AccountRepository
import anissia.domain.anime.core.AnimeGenre
import anissia.domain.anime.core.ports.outbound.AnimeGenreRepository
import anissia.domain.board.core.BoardTicker
import anissia.domain.board.core.ports.outbound.BoardTickerRepository
import org.springframework.context.annotation.Profile
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


/**
 * 이 컨트롤러는 개발환경에서만 사용할 수 있다.
 * 설치 전용 컨트롤러로 컨트롤러에 로직이 포함된다.
 */
@Profile("dev", "local")
@RestController
class InstallController(
    private val accountRepository: AccountRepository,
    private val animeGenreRepository: AnimeGenreRepository,
    private val boardTickerRepository: BoardTickerRepository,
) {
    @Transactional
    @GetMapping("/install", produces = ["text/plain"])
    fun install(): String {

        val sb = StringBuilder()

        sb.append("설치를 시작합니다. (개발중)\n")

        if (accountRepository.count() == 0L) {
            sb.append("계정을 생성합니다.\n")
        } else {
            sb.append("이미 계정이 생성되어 있습니다.\n")
        }

        if (animeGenreRepository.count() == 0L) {
            listOf("BL", "GL", "OTT", "SF", "TS", "개그", "게임", "고어", "공포", "금융", "기타", "내정", "드라마", "레이싱", "로맨스", "마법소녀", "메르헨", "메카닉", "모험", "무협", "미소녀", "미스터리", "밀리터리", "변신", "순정", "스릴러", "스포츠", "시대물", "아동", "아이돌", "액션", "연애", "요괴", "우주", "음악", "이세계", "일상", "일어더빙", "추리", "치유", "코미디", "코스프레", "판타지", "패러디", "퍼즐", "학원", "현대", "호러", "환경")
                .map { AnimeGenre(it) }
                .also { animeGenreRepository.saveAll(it) }
            sb.append("장르를 생성 했습니다.\n")
        } else {
            sb.append("이미 장르가 생성되어 있습니다.\n")
        }

        if (boardTickerRepository.count() == 0L) {
            boardTickerRepository.save(BoardTicker("inquiry", "문의 게시판", "", "", "본 게시판은 애니시아 사이트에 대한 문의를 올리는 장소입니다."))
            boardTickerRepository.save(BoardTicker("notice", "공지사항", "ROOT,TRANSLATOR", "", "내용"))
            sb.append("게시판을 생성 했습니다.\n")
        } else {
            sb.append("이미 게시판이 생성되어 있습니다.\n")
        }

        return sb.toString()
    }
}
