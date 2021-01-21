package anissia.services

import anissia.elasticsearch.domain.AnimeDocument
import anissia.elasticsearch.repository.AnimeDocumentRepository
import anissia.rdb.domain.Anime
import anissia.rdb.dto.AnimeCaptionDto
import anissia.rdb.dto.AnimeDto
import anissia.rdb.repository.AnimeCaptionRepository
import anissia.rdb.repository.AnimeRepository
import me.saro.kit.CacheStore
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.lang.StringBuilder
import javax.servlet.http.HttpServletRequest
import kotlin.streams.toList

@Service
class AnimeService(
    private val animeRepository: AnimeRepository,
    private val animeDocumentRepository: AnimeDocumentRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeRankService: AnimeRankService,
    private val request: HttpServletRequest
) {

    private val captionCacheStore = CacheStore<Long, List<AnimeCaptionDto>>((5 * 60000).toLong())

    fun getList(q: String, page: Int): Page<AnimeDto> =
        if (q.isNotBlank()) {
            val subject = StringBuilder(100)
            val tags = StringBuilder(100)
            q.split("\\s+").parallelStream().map { it.trim() }.filter { it.isNotEmpty() }.sequential().forEach {
                if (it[0] == '#') {
                    tags.append(it.substring(1)).append(' ')
                } else {
                    subject.append(it).append(' ')
                }
            }
            val page = animeDocumentRepository.findAllBySubjectLikeAndGenresLike(subject.toString(), tags.toString(), PageRequest.of(page, 20))
            PageImpl(animeRepository.findAllByIdInOrderByAnimeNoDesc(page.get().map { it.animeNo }.toList()).map { AnimeDto(it) }, page.pageable, page.totalElements)
        } else {
            animeRepository.findAllByOrderByAnimeNoDesc(PageRequest.of(page, 20)).map { AnimeDto(it) }
        }


    fun getDelist(page: Int): Page<AnimeDto> =
        animeRepository.findAllDelByOrderByAnimeNoDesc(PageRequest.of(page, 20)).map { AnimeDto(it) }

    fun getAnime(animeNo: Long): AnimeDto =
        animeRepository.findWithCaptionsByAnimeNo(animeNo)
            ?.let { AnimeDto(it, true) }
            ?.also { animeRankService.hitAsync(it.animeNo, request.remoteAddr) }
            ?: AnimeDto()

    fun getCaptionByAnimeNo(animeNo: Long): List<AnimeCaptionDto> =
        captionCacheStore
            .get(animeNo) { animeCaptionRepository.findAllWithAccountByAnimeNoOrderByUpdDtDesc(animeNo).map { AnimeCaptionDto(it) } }
            .also { animeRankService.hitAsync(animeNo, request.remoteAddr) }

    fun updateDocument(animeNo: Long) = animeRepository.findByIdOrNull(animeNo)?.also { updateDocument(it) }

    fun updateDocument(anime: Anime) =
        animeDocumentRepository
            .findById(anime.animeNo)
            .orElseGet { AnimeDocument(animeNo = anime.animeNo) }
            .also {
                it.animeNo = anime.animeNo
                it.subject = anime.subject
                it.genres = anime.genres.replace(",", " ")
                animeDocumentRepository.save(it)
            }
}