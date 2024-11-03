package anissia.domain.anime.controller


import anissia.domain.anime.model.*
import anissia.domain.anime.service.AnimeGenreGenres
import anissia.domain.anime.service.AnimeRankService
import anissia.domain.anime.service.AnimeService
import anissia.domain.session.model.Session
import anissia.infrastructure.common.As
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/anime")
class AnimeController(
    private val animeService: AnimeService,
    private val animeGenreGenres: AnimeGenreGenres,
    private val animeRankService: AnimeRankService,
) {
    @GetMapping("/list/{page:\\d+}")
    fun getAnimeList(cmd: GetAnimeListCommand, exchange: ServerWebExchange): ResultWrapper<Page<AnimeItem>> =
        ResultWrapper.ok(animeService.getList(cmd))

    @GetMapping("/delist")
    fun getAnimeDelist(exchange: ServerWebExchange): ResultWrapper<Page<AnimeItem>> =
        ResultWrapper.ok(animeService.getDelist(As.toSession(exchange)))

    @GetMapping("/animeNo/{animeNo:\\d+}")
    fun getAnime(cmd: GetAnimeCommand, session: Session, exchange: ServerWebExchange): ResultWrapper<AnimeItem> =
        ResultWrapper.ok(animeService.get(cmd, session))

    @GetMapping("/autocorrect")
    fun getAnimeAutocorrect(cmd: GetAutocorrectAnimeCommand, exchange: ServerWebExchange): ResultWrapper<List<String>> =
        ResultWrapper.ok(animeService.getAutocorrect(cmd))

    @GetMapping("/genres")
    fun getGenres(exchange: ServerWebExchange): ResultWrapper<List<String>> =
        ResultWrapper.ok(animeGenreGenres.get())

    @GetMapping("/rank/{type}")
    fun getAnimeRank(cmd: GetAnimeRankCommand, exchange: ServerWebExchange): ResultWrapper<List<Map<*,*>>> =
        ResultWrapper.ok(animeRankService.get(cmd))

    @DeleteMapping("/{animeNo}")
    fun deleteAnime(cmd: DeleteAnimeCommand, exchange: ServerWebExchange): ResultWrapper<Unit> =
        animeService.delete(cmd, As.toSession(exchange))

    @PostMapping
    fun newAnime(@RequestBody cmd: NewAnimeCommand, exchange: ServerWebExchange): ResultWrapper<Long> =
        animeService.add(cmd, As.toSession(exchange))

    @PutMapping("/{animeNo}")
    fun editAnime(@RequestBody cmd: EditAnimeCommand, @PathVariable animeNo: Long, exchange: ServerWebExchange): ResultWrapper<Long> =
        animeService.edit(cmd.apply { this.animeNo = animeNo }, As.toSession(exchange))

    @PostMapping("/recover/{agendaNo}")
    fun recoverAnime(cmd: RecoverAnimeCommand, exchange: ServerWebExchange): ResultWrapper<Long> =
        animeService.recover(cmd, As.toSession(exchange))
}
