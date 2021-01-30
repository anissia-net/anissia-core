package anissia.services

import anissia.configruration.logger
import anissia.elasticsearch.domain.AnimeDocument
import anissia.elasticsearch.repository.AnimeDocumentRepository
import anissia.misc.As
import anissia.rdb.domain.Anime
import anissia.rdb.dto.AnimeCaptionDto
import anissia.rdb.dto.AnimeDto
import anissia.rdb.repository.AnimeCaptionRepository
import anissia.rdb.repository.AnimeRepository
import anissia.rdb.repository.AnimeTempRepository
import me.saro.kit.CacheStore
import me.saro.kit.lang.Koreans
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import kotlin.streams.toList

@Service
class AnimeService(
    private val animeRepository: AnimeRepository,
    private val animeTempRepository: AnimeTempRepository,
    private val animeDocumentRepository: AnimeDocumentRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeRankService: AnimeRankService,
    private val request: HttpServletRequest
) {

    private val log = logger<AnimeService>()
    private val captionCacheStore = CacheStore<Long, List<AnimeCaptionDto>>((5 * 60000).toLong())
    private val autocorrectStore = CacheStore<String, String>(60 * 60000)

    fun getList(q: String, page: Int): Page<AnimeDto> =
        if (q.isNotBlank()) {
            val keywords = ArrayList<String>()
            val genres = ArrayList<String>()

            q.split("[\\s]+".toRegex()).stream().map { it.trim() }.filter { it.isNotEmpty() }.forEach { word ->
                if (word[0] == '#' && word.length > 1) genres.add(word.substring(1))
                else keywords.add(word)
            }

            val page = animeDocumentRepository.findAllAnimeNoForAnimeSearch(keywords, genres, PageRequest.of(page, 20))

            log.info("anime search $keywords $genres ${page.totalElements}")

            PageImpl(animeRepository.findAllByAnimeNoInOrderByAnimeNoDesc(page.get().toList()).map { AnimeDto(it) }, page.pageable, page.totalElements)
        } else {
            animeRepository.findAllByOrderByAnimeNoDesc(PageRequest.of(page, 20)).map { AnimeDto(it) }
        }

    fun getAnimeAutocorrect(q: String): String =
        if (q.length < 3) autocorrectStore.find(q) { getAnimeAutocorrectPrivate(q) }
        else  getAnimeAutocorrectPrivate(q)

    private fun getAnimeAutocorrectPrivate(q: String): String =
        q?.let { it.replace("%", "").trim() }
            ?.takeIf { it.isNotEmpty() }
            ?.let { As.toJsonString(animeRepository.findTop10ByAutocorrectStartsWith(Koreans.toJasoAtom(it))) }
            ?: "[]"

    fun clearAnimeAutocorrect() = autocorrectStore.clear()

    fun getDelist(page: Int): Page<AnimeDto> =
        animeTempRepository.findAllDelByOrderByAnimeNoDesc(PageRequest.of(page, 20)).map { AnimeDto(it) }

    fun getAnime(animeNo: Long): AnimeDto =
        animeRepository.findWithCaptionsByAnimeNo(animeNo)
            ?.let { AnimeDto(it, true) }
            ?.also { animeRankService.hitAsync(it.animeNo, request.remoteAddr) }
            ?: AnimeDto()

    fun getAnimeTemp(animeNo: Long): AnimeDto =
        animeTempRepository.findWithCaptionsByAnimeNo(animeNo)
            ?.let { AnimeDto(it, true) }
            ?: AnimeDto()

    fun getCaptionByAnimeNo(animeNo: Long): List<AnimeCaptionDto> =
        captionCacheStore
            .find(animeNo) { animeCaptionRepository.findAllWithAccountByAnimeNoOrderByUpdDtDesc(animeNo).map { AnimeCaptionDto(it) } }
            .also { animeRankService.hitAsync(animeNo, request.remoteAddr) }

    fun updateDocument(animeNo: Long) = animeRepository.findByIdOrNull(animeNo)?.also { updateDocument(it) }

    fun updateDocument(anime: Anime) =
        animeDocumentRepository
            .findById(anime.animeNo)
            .orElseGet { AnimeDocument(animeNo = anime.animeNo) }
            .also {
                it.animeNo = anime.animeNo
                it.subject = anime.subject
                it.genres = anime.genres.split(",".toRegex())
                animeDocumentRepository.save(it)
            }
}