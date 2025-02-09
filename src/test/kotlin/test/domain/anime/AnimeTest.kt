package test.domain.anime


import anissia.domain.anime.service.AnimeGenreGenres
import anissia.domain.anime.service.AnimeRankService
import anissia.domain.anime.service.AnimeService

class AnimeTest(
    private val animeService: AnimeService,
    private val animeGenreGenres: AnimeGenreGenres,
    private val animeRankService: AnimeRankService,
) {
//    @GetMapping("/list/{page:\\d+}")
//    fun getAnimeList(cmd: GetAnimeListCommand, exchange: ServerWebExchange): Mono<ApiResponse<Page<AnimeItem>> =
//        getAnimeList.handle(cmd))
//
//    @GetMapping("/delist")
//    fun getAnimeDelist(exchange: ServerWebExchange): Mono<ApiResponse<Page<AnimeItem>> =
//        getAnimeDelist.handle(exchange.sessionItem))
//
//    @GetMapping("/animeNo/{animeNo:\\d+}")
//    fun getAnime(cmd: GetAnimeCommand, session: Session, exchange: ServerWebExchange): Mono<ApiResponse<AnimeItem> =
//        getAnime.handle(cmd, session))
//
//    @GetMapping("/autocorrect")
//    fun getAnimeAutocorrect(cmd: GetAnimeAutocorrectCommand, exchange: ServerWebExchange): Mono<ApiResponse<List<String>> =
//        getAnimeAutocorrect.handle(cmd))
//
//    @GetMapping("/genres")
//    fun getGenres(exchange: ServerWebExchange): Mono<ApiResponse<List<String>> =
//        getGenres.handle())
//
//    @GetMapping("/rank/{type}")
//    fun getAnimeRank(cmd: GetAnimeRankCommand, exchange: ServerWebExchange): Mono<ApiResponse<List<Map<*,*>>> =
//        getAnimeRank.handle(cmd))
//
//    @DeleteMapping("/{animeNo}")
//    fun deleteAnime(cmd: DeleteAnimeCommand, exchange: ServerWebExchange): Mono<String> =
//        deleteAnime.handle(cmd, exchange.sessionItem)
//
//    @PostMapping
//    fun newAnime(@RequestBody cmd: NewAnimeCommand, exchange: ServerWebExchange): Mono<ApiResponse<Long> =
//        newAnime.handle(cmd, exchange.sessionItem)
//
//    @PutMapping("/{animeNo}")
//    fun editAnime(@RequestBody cmd: EditAnimeCommand, @PathVariable animeNo: Long, exchange: ServerWebExchange): Mono<ApiResponse<Long> =
//        editAnime.handle(cmd.apply { this.animeNo = animeNo }, exchange.sessionItem)
//
//    @PostMapping("/recover/{agendaNo}")
//    fun recoverAnime(cmd: RecoverAnimeCommand, exchange: ServerWebExchange): Mono<ApiResponse<Long> =
//        recoverAnime.handle(cmd, exchange.sessionItem)
}
