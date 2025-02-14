package anissia.domain.anime.service

import anissia.domain.account.repository.AccountRepository
import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.repository.ActivePanelRepository
import anissia.domain.activePanel.service.ActivePanelService
import anissia.domain.agenda.Agenda
import anissia.domain.agenda.repository.AgendaRepository
import anissia.domain.anime.Anime
import anissia.domain.anime.AnimeCaption
import anissia.domain.anime.AnimeStatus
import anissia.domain.anime.command.*
import anissia.domain.anime.model.AnimeItem
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.anime.repository.AnimeGenreRepository
import anissia.domain.anime.repository.AnimeRepository
import anissia.domain.session.model.SessionItem
import anissia.domain.translator.service.TranslatorApplyService
import anissia.infrastructure.common.*
import anissia.infrastructure.service.ElasticsearchService
import anissia.shared.ApiFailException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import me.saro.kit.lang.KoreanKit
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.util.*

@Service
class AnimeServiceImpl(
    private val animeRepository: AnimeRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeDocumentService: AnimeDocumentService,
    private val animeRankService: AnimeRankService,
    private val activePanelService: ActivePanelService,
    private val agendaRepository: AgendaRepository,
    private val translatorApplyService: TranslatorApplyService,
    private val animeGenreRepository: AnimeGenreRepository,
    private val activePanelRepository: ActivePanelRepository,
    private val elasticsearch: ElasticsearchService,
    private val accountRepository: AccountRepository,
): AnimeService {

    private val log = logger<AnimeServiceImpl>()
    private val autocorrectStore = MonoCacheStore<String, List<String>>(60 * 60000)
    private val trAnimeItem = object: TypeReference<AnimeItem>(){}
    private val mapper = ObjectMapper()

    override fun get(cmd: GetAnimeCommand, sessionItem: SessionItem): Mono<AnimeItem> =
        Mono.justOrEmpty<Anime>(animeRepository.findWithCaptionsByAnimeNo(cmd.animeNo))
            .map { AnimeItem(it, true) }
            .doOnNext { animeRankService.hit(HitAnimeCommand(cmd.animeNo), sessionItem).subscribeBoundedElastic() }
            .switchIfEmpty(Mono.just(AnimeItem()))

    override fun getList(cmd: GetAnimeListCommand): Mono<Page<AnimeItem>> =
        Mono.defer {
            val q = cmd.q
            val page = cmd.page

            if (q.isNotBlank()) {
                val keywords = ArrayList<String>()
                val genres = ArrayList<String>()
                val translators = ArrayList<String>()
                val end = q.indexOf("/완결") != -1

                q.lowercase(Locale.getDefault())
                    .split("\\s+".toRegex())
                    .stream()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() && it != "/완결" }
                    .forEach { word ->
                        if (word[0] == '#' && word.length > 1) genres.add(word.substring(1))
                        else if (word[0] == '@' && word.length > 1) translators.add(word.substring(1))
                        else keywords.add(word)
                    }

                val req = (mapper.createObjectNode().apply {
                    put("_source", false)
                    putObject("query").apply {
                        putObject("bool").apply {
                            put("minimum_should_match", "100%")

                            if (keywords.isNotEmpty() || genres.isNotEmpty()) {
                                putArray("must").apply {
                                    keywords.forEach {
                                        addObject().apply { putObject("wildcard").apply { put("subject", "*$it*") } }
                                    }
                                    genres.forEach {
                                        addObject().apply { putObject("match").apply { put("genres", it) } }
                                    }
                                }
                            }

                            if (translators.isNotEmpty() || end) {
                                putArray("filter").apply {

                                    if (translators.isNotEmpty()) {
                                        addObject().apply { putObject("bool").apply {
                                            putArray("should").apply {
                                                translators.forEach {
                                                    addObject().apply { putObject("match").apply { put("translators", it) } }
                                                }
                                            }
                                            put("minimum_should_match", "1")
                                        } }
                                    }

                                    if (end) {
                                        addObject().apply { putObject("match").apply { put("status", "END") } }
                                    }
                                }
                            }
                        }
                    }
                    // 종료된 애니만 검색
                    if (end) {
                        putArray("sort").apply { addObject().apply { putObject("endDate").apply { put("order", "desc") } } }
                    }
                    put("from", page * 30)
                    put("size", 30)
                }).toJson

//            """{
//                "_source": false,
//                "query": {
//                    "bool": {
//                        "minimum_should_match": "100%",
//                        "must": [
//                            ${keywords.joinToString(",") { """{"wildcard": {"subject": "*${it.es}*"}}""" }},
//                            ${genres.joinToString(",") { """{"match": {"genres": "${it.es}"}}""" }}
//                        ],
//                        "filter": [
//                            ${translators.joinToString(",") { """{"bool": {"should": [${"""{"match": {"translators": "${it.es}"}}"""}], "minimum_should_match": "1"}}""" }},
//                            ${if (end) """{"match": {"status": "END"}}""" else ""}
//                        ]
//                    }
//                },
//                ${if (end) """{"sort": [{"endDate": {"order": "desc"}}]}""" else ""}
//                "from": ${page * 30},
//                "size": 30
//            }"""

                log.info(req)

                elasticsearch.request(HttpMethod.POST, "/anissia_anime/_search", req)
                    .map { res ->
                        val hits = res["hits"]
                        val result = PageImpl<Long>(hits["hits"].map { it["_id"].asLong() }, PageRequest.of(page, 30), hits["total"]["value"].asLong())
                        log.info("anime search $keywords $genres $translators $end ${result.totalElements}")
                        result.replacePage(animeRepository.findAllByAnimeNoInOrderByAnimeNoDesc(result.content).map { AnimeItem(it) })
                    }
            } else {
                Mono.just(animeRepository.findAllByOrderByAnimeNoDesc(PageRequest.of(page, 30)).map { AnimeItem(it) })
            }
        }


    override fun getDelist(sessionItem: SessionItem): Mono<Page<AnimeItem>> =
        Mono.fromCallable {
            sessionItem.validateAdmin()
            agendaRepository.findAllByCodeAndStatusOrderByAgendaNoDesc("ANIME-DEL", "wait")
                .map { it.data1!!.toClassByJson(trAnimeItem).apply { this.agendaNo = it.agendaNo } }
        }


    override fun getAutocorrect(cmd: GetAutocorrectAnimeCommand): Mono<List<String>> {
        val q = cmd.q
        return if (q.length < 3) autocorrectStore.find(q) { getAnimeAutocorrectPrivate(q) }
        else  getAnimeAutocorrectPrivate(q)
    }

    private fun getAnimeAutocorrectPrivate(q: String): Mono<List<String>> =
        Mono.just(q.replace("%", "").trim())
            .filter { it.isNotEmpty() }
            .map { animeRepository.findTop10ByAutocorrectStartsWith(KoreanKit.toJasoAtom(it)) }
            .switchIfEmpty(Mono.just(listOf()))

    @Transactional
    override fun add(cmd: NewAnimeCommand, sessionItem: SessionItem): Mono<Long> =
        Mono.defer {
            cmd.validate()
            sessionItem.validateAdmin()
            translatorApplyService.getGrantedTime(sessionItem.an)
                .flatMap {
                    if (it.isBefore(OffsetDateTime.now().minusDays(90))) {
                        Mono.error(ApiFailException("애니메이션 등록은 권한 취득일로부터 90일 후에 가능합니다.", -1))
                    } else {
                        Mono.just(it)
                    }
                }
                .flatMap {
                    if (animeGenreRepository.countByGenreIn(cmd.genresList).toInt() != cmd.genresList.size) {
                        Mono.error(ApiFailException("장르 입력이 잘못되었습니다.", -1))
                    } else {
                        Mono.just(it)
                    }
                }
                .flatMap {
                    if (animeRepository.existsBySubject(cmd.subject)) {
                        Mono.error(ApiFailException("이미 동일한 이름의 작품이 존재합니다.", -1))
                    } else {
                        Mono.just(it)
                    }
                }
                .map {
                    val anime = Anime(
                        status = cmd.statusEnum,
                        week = cmd.week,
                        time = cmd.time,
                        subject = cmd.subject,
                        originalSubject = cmd.originalSubject,
                        autocorrect = KoreanKit.toJasoAtom(cmd.subject),
                        genres = cmd.genres,
                        startDate = cmd.startDate,
                        endDate = cmd.endDate,
                        website = cmd.website,
                        twitter = cmd.twitter,
                    )
                    val activePanel = ActivePanel(
                        published = true,
                        code = "ANIME",
                        status = "C",
                        an = sessionItem.an,
                        data1 = "[${sessionItem.name}]님이 애니메이션 [${anime.subject}]을(를) 추가하였습니다."
                    )

                    animeRepository.save(anime)
                    activePanelRepository.save(activePanel)
                    animeDocumentService.update(anime, false).subscribeBoundedElastic()

                    anime.animeNo
                }
        }

    @Transactional
    override fun edit(cmd: EditAnimeCommand, sessionItem: SessionItem): Mono<Long> =
        Mono.fromCallable {
            cmd.validate()
            sessionItem.validateAdmin()
            translatorApplyService.getGrantedTime(sessionItem.an)
                .flatMap {
                    if (it.isBefore(OffsetDateTime.now().minusDays(90))) {
                        Mono.error(ApiFailException("애니메이션 편집은 권한 취득일로부터 90일 후에 가능합니다.", -1))
                    } else {
                        Mono.just(it)
                    }
                }.flatMap {
                    val animeNo = cmd.animeNo

                    if (animeGenreRepository.countByGenreIn(cmd.genresList).toInt() != cmd.genresList.size) {
                        Mono.error(ApiFailException("장르 입력이 잘못되었습니다.", -1))
                    } else if (animeRepository.existsBySubjectAndAnimeNoNot(cmd.subject, animeNo)) {
                        Mono.error(ApiFailException("이미 동일한 이름의 작품이 존재합니다.", -1)
                    } else {
                        Mono.just(animeNo)
                    }
                }.flatMap { animeNo ->
                    val activePanel = ActivePanel(
                        published = true,
                        code = "ANIME",
                        status = "U",
                        an = sessionItem.an,
                        data1 = "[${sessionItem.name}]님이 애니메이션 [${cmd.subject}]을(를) 수정하였습니다."
                    )

                    val anime = animeRepository.findByIdOrNull(animeNo)
                        ?.also {
                            if (
                                it.week == cmd.week &&
                                it.status == cmd.statusEnum &&
                                it.time == cmd.time &&
                                it.subject == cmd.subject &&
                                it.originalSubject == cmd.originalSubject &&
                                it.genres == cmd.genres &&
                                it.startDate == cmd.startDate &&
                                it.endDate == cmd.endDate &&
                                it.website == cmd.website &&
                                it.twitter == cmd.twitter
                            ) {
                                return@flatMap Mono.error(ApiFailException("변경사항이 없습니다.", -1))
                            }
                        }
                        ?.also { activePanel.data2 = toJsonString(AnimeItem(it, false), mapOf("note" to "")) }
                        ?.apply {
                            status = cmd.statusEnum
                            week = cmd.week
                            time = cmd.time
                            subject = cmd.subject
                            originalSubject = cmd.originalSubject
                            autocorrect = KoreanKit.toJasoAtom(cmd.subject)
                            genres = cmd.genres
                            startDate = cmd.startDate
                            endDate = cmd.endDate
                            website = cmd.website
                            twitter = cmd.twitter
                        }
                        ?.also { activePanel.data3 = toJsonString(AnimeItem(it, false), mapOf("note" to cmd.note)) }
                        ?: return Mono.error(ApiFailException("존재하지 않는 애니메이션입니다.", -1)

                            animeRepository.save(anime)
                            activePanelRepository.save(activePanel)
                            animeDocumentService.update(anime)

                            return ResultWrapper.of("ok", "", anime.animeNo)
                }
        }

    @Transactional
    override fun delete(cmd: DeleteAnimeCommand, sessionItem: SessionItem): Mono<String> =
        Mono.fromCallable {
            cmd.validate()
            sessionItem.validateAdmin()
            translatorApplyService.getGrantedTime(sessionItem.an)
                ?.takeIf { it.isBefore(OffsetDateTime.now().minusDays(90)) }
                ?: return Mono.error(ApiFailException("애니메이션 삭제는 권한 취득일로부터 90일 후에 가능합니다.")

                    val animeNo = cmd.animeNo
            val agenda = Agenda(code = "ANIME-DEL", status = "wait", an = sessionItem.an)

            val anime = animeRepository.findWithCaptionsByAnimeNo(animeNo)
                ?.also { agenda.data1 = toJsonString(AnimeItem(it, true)) }
                ?: return Mono.error(ApiFailException("존재하지 않는 애니메이션입니다.")

                    activePanelService.addText(AddTextActivePanelCommand("[${sessionItem.name}]님이 애니메이션 [${anime.subject}]을(를) 삭제하였습니다."), null)

            animeCaptionRepository.deleteByAnimeNo(animeNo)
            animeRepository.delete(anime)
            animeDocumentService.update(anime)
            agendaRepository.save(agenda)
        }


    @Transactional
    override fun recover(cmd: RecoverAnimeCommand, sessionItem: SessionItem) =
        Mono.fromCallable {
            cmd.validate()
            sessionItem.validateAdmin()

            val agenda = agendaRepository
                .findByIdOrNull(cmd.agendaNo)?.takeIf { it.code == "ANIME-DEL" && it.status == "wait" }
                ?: return Mono.error(ApiFailException("이미 복원되었거나 존재하지 않는 애니메이션입니다.", -1)

                    val animeItem = OBJECT_MAPPER.readValue(agenda.data1, object: TypeReference<AnimeItem>() {})

            if (animeRepository.existsById(animeItem.animeNo)) {
                return Mono.error(ApiFailException("이미 복원되었거나 존재하지 않는 애니메이션입니다.", -1)
            }
            if (animeRepository.existsBySubject(animeItem.subject)) {
                return Mono.error(ApiFailException("이미 해당 제목의 에니메이션이 있습니다.", -1)
            }

            val anime = animeRepository.save(
                Anime(
                    status = AnimeStatus.valueOf(animeItem.status),
                    week = animeItem.week,
                    time = animeItem.time,
                    subject = animeItem.subject,
                    originalSubject = animeItem.originalSubject,
                    autocorrect = KoreanKit.toJasoAtom(animeItem.subject),
                    genres = animeItem.genres,
                    startDate = animeItem.startDate,
                    endDate = animeItem.endDate,
                    website = animeItem.website,
                    twitter = animeItem.twitter,
                    captionCount = animeItem.captionCount
                )
            )

            animeItem.captions.forEach { caption ->
                val account = accountRepository.findWithRolesByName(caption.name)
                if (account?.isAdmin == true) {
                    animeCaptionRepository.save(
                        AnimeCaption(
                            anime = anime,
                            an = account.an,
                            episode = caption.episode,
                            updDt = OffsetDateTime.parse(caption.updDt, DTF_ISO_YMDHMS),
                            website = caption.website
                        )
                    )
                }
            }

            activePanelService.addText(AddTextActivePanelCommand("[${sessionItem.name}]님이 애니메이션 [${anime.subject}]을(를) 복원하였습니다."), null)

            animeDocumentService.update(anime)
            animeRepository.updateCaptionCount(anime.animeNo)
            agendaRepository.save(agenda.apply { status = "recover" })

            return ResultWrapper.of("ok", "", anime.animeNo)
        }
}
