package anissia.services

import anissia.configruration.logger
import anissia.dto.AnimeCaptionDto
import anissia.dto.AnimeDto
import anissia.elasticsearch.document.AnimeDocument
import anissia.elasticsearch.repository.AnimeDocumentRepository
import anissia.misc.As
import anissia.rdb.entity.Anime
import anissia.rdb.repository.AnimeCaptionRepository
import anissia.rdb.repository.AnimeGenreRepository
import anissia.rdb.repository.AnimeRepository
import me.saro.kit.CacheStore
import me.saro.kit.lang.Koreans
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ConcurrentHashMap
import javax.servlet.http.HttpServletRequest

@Service
class AnimeService(
    private val animeRepository: AnimeRepository,
    private val animeDocumentRepository: AnimeDocumentRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeGenreRepository: AnimeGenreRepository,
    private val animeRankService: AnimeRankService,
    private val request: HttpServletRequest
) {

    private val log = logger<AnimeService>()
    private val autocorrectStore = CacheStore<String, String>(60 * 60000)
    private val genresCacheStore = CacheStore<String, String>(60 * 60000)

    fun getList(q: String, page: Int): Page<AnimeDto> =
        if (q.isNotBlank()) {
            val keywords = ArrayList<String>()
            val genres = ArrayList<String>()
            val translators = ArrayList<String>()
            val end = q.indexOf("/완결") != -1

            q.toLowerCase().split("[\\s]+".toRegex()).stream().map { it.trim() }.filter { it.isNotEmpty() && it != "/완결" }.forEach { word ->
                if (word[0] == '#' && word.length > 1) genres.add(word.substring(1))
                else if (word[0] == '@' && word.length > 1) translators.add(word.substring(1))
                else keywords.add(word)
            }

            val result = animeDocumentRepository.findAllAnimeNoForAnimeSearch(keywords, genres, translators, end, PageRequest.of(page, 20))

            log.info("anime search $keywords $genres $translators $end ${result.totalElements}")

            As.replacePage(result, animeRepository.findAllByAnimeNoInOrderByAnimeNoDesc(result.content).map { AnimeDto(it) })
        } else {
            animeRepository.findAllByOrderByAnimeNoDesc(PageRequest.of(page, 20)).map { AnimeDto(it) }
        }

    fun getAnimeAutocorrect(q: String): String =
        if (q.length < 3) autocorrectStore.find(q) { getAnimeAutocorrectPrivate(q) }
        else  getAnimeAutocorrectPrivate(q)

    private fun getAnimeAutocorrectPrivate(q: String): String =
        q.let { it.replace("%", "").trim() }
            .takeIf { it.isNotEmpty() }
            ?.let { As.toJsonString(animeRepository.findTop10ByAutocorrectStartsWith(Koreans.toJasoAtom(it))) }
            ?: "[]"

    fun listAnimeAutocorrect(): String =
        autocorrectStore.javaClass.getField("store").run {
            isAccessible = true
            (get(autocorrectStore) as ConcurrentHashMap<String, Any>).keys.toList().sorted().joinToString("\n")
        }

    fun clearAnimeAutocorrect() = autocorrectStore.clear()

    fun getAnime(animeNo: Long): AnimeDto =
        animeRepository.findWithCaptionsByAnimeNo(animeNo)
            ?.let { AnimeDto(it, true) }
            ?.also { animeRankService.hitAsync(it.animeNo, request.remoteAddr) }
            ?: AnimeDto()


    fun getGenres() =
        genresCacheStore.find("genre") { animeGenreRepository.findAll().map { it.genre }.apply { sorted() }.let { As.toJsonString(it) } }

    fun getCaptionByAnimeNo(animeNo: Long): List<AnimeCaptionDto> =
        animeCaptionRepository.findAllWithAccountByAnimeNoOrderByUpdDtDesc(animeNo).map { AnimeCaptionDto(it) }
            .also { animeRankService.hitAsync(animeNo, request.remoteAddr) }

    fun updateDocument(animeNo: Long) = animeRepository.findByIdOrNull(animeNo)?.also { updateDocument(it) }

    @Transactional
    fun updateDocument(anime: Anime): AnimeDocument =
        animeDocumentRepository
            .findById(anime.animeNo)
            .orElseGet { AnimeDocument(animeNo = anime.animeNo) }
            .also {
                it.animeNo = anime.animeNo
                it.subject = anime.subject
                it.status = anime.status.name
                it.genres = anime.genres.split(",".toRegex())
                it.translators = animeCaptionRepository.findAllTranslatorByAnimeNo(anime.animeNo)
                it.endDate = anime.endDate.replace("-", "").run { if (isEmpty()) 0L else toLong() }
                animeDocumentRepository.save(it)
            }

    fun deleteDocument(animeNo: Long) = animeDocumentRepository.deleteById(animeNo)
}
