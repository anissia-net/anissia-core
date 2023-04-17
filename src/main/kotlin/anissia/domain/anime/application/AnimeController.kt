package anissia.domain.anime.application


import anissia.domain.anime.core.model.*
import anissia.domain.anime.core.ports.inbound.*
import anissia.domain.session.core.model.Session
import anissia.infrastructure.common.As
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/anime")
class AnimeController(
    private val getAnime: GetAnime,
    private val getAnimeAutocorrect: GetAnimeAutocorrect,
    private val getAnimeList: GetAnimeList,
    private val getAnimeDelist: GetAnimeDelist,
    private val getGenres: GetGenres,
    private val getAnimeRank: GetAnimeRank,
    private val deleteAnime: DeleteAnime,
    private val newAnime: NewAnime,
    private val editAnime: EditAnime,
    private val recoverAnime: RecoverAnime,
) {
    @GetMapping("/list/{page:\\d+}")
    fun getAnimeList(cmd: GetAnimeListCommand, exchange: ServerWebExchange): ResultWrapper<Page<AnimeItem>> =
        ResultWrapper.ok(getAnimeList.handle(cmd))

    @GetMapping("/delist")
    fun getAnimeDelist(exchange: ServerWebExchange): ResultWrapper<Page<AnimeItem>> =
        ResultWrapper.ok(getAnimeDelist.handle(As.toSession(exchange)))

    @GetMapping("/animeNo/{animeNo:\\d+}")
    fun getAnime(cmd: GetAnimeCommand, session: Session, exchange: ServerWebExchange): ResultWrapper<AnimeItem> =
        ResultWrapper.ok(getAnime.handle(cmd, session))

    @GetMapping("/autocorrect")
    fun getAnimeAutocorrect(cmd: GetAnimeAutocorrectCommand, exchange: ServerWebExchange): ResultWrapper<List<String>> =
        ResultWrapper.ok(getAnimeAutocorrect.handle(cmd))

    @GetMapping("/genres")
    fun getGenres(exchange: ServerWebExchange): ResultWrapper<List<String>> =
        ResultWrapper.ok(getGenres.handle())

    @GetMapping("/rank/{type}")
    fun getAnimeRank(cmd: GetAnimeRankCommand, exchange: ServerWebExchange): ResultWrapper<List<Map<*,*>>> =
        ResultWrapper.ok(getAnimeRank.handle(cmd))

    @DeleteMapping("/{animeNo}")
    fun deleteAnime(cmd: DeleteAnimeCommand, exchange: ServerWebExchange): ResultWrapper<Unit> =
        deleteAnime.handle(cmd, As.toSession(exchange))

    @PostMapping
    fun newAnime(@RequestBody cmd: NewAnimeCommand, exchange: ServerWebExchange): ResultWrapper<Long> =
        newAnime.handle(cmd, As.toSession(exchange))

    @PutMapping("/{animeNo}")
    fun editAnime(@RequestBody cmd: EditAnimeCommand, @PathVariable animeNo: Long, exchange: ServerWebExchange): ResultWrapper<Long> =
        editAnime.handle(cmd.apply { this.animeNo = animeNo }, As.toSession(exchange))

    @PostMapping("/recover/{agendaNo}")
    fun recoverAnime(cmd: RecoverAnimeCommand, exchange: ServerWebExchange): ResultWrapper<Long> =
        recoverAnime.handle(cmd, As.toSession(exchange))
}
