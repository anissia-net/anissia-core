package anissia.services

import anissia.rdb.dto.AnimeCaptionDto
import anissia.rdb.dto.AnimeDto
import anissia.rdb.repository.AnimeCaptionRepository
import anissia.rdb.repository.AnimeRepository
import me.saro.kit.CacheStore
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class AnimeService(
    private val animeRepository: AnimeRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeRankService: AnimeRankService,
    private val request: HttpServletRequest
) {

    private val captionCacheStore = CacheStore<Long, List<AnimeCaptionDto>>((5 * 60000).toLong())

    fun getList(q: String, page: Int): Page<AnimeDto> =
        animeRepository.findAllByOrderByAnimeNoDesc(PageRequest.of(page, 20)).map { AnimeDto(it) }

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

}