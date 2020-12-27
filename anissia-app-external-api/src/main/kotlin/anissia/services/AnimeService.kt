package anissia.services

import anissia.dto.AnimeCaptionDto
import anissia.repository.AnimeCaptionRepository
import anissia.repository.AnimeRepository
import me.saro.kit.CacheStore
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

    fun getCaptionByAnimeNo(animeNo: Long): List<AnimeCaptionDto> =
        captionCacheStore
            .get(animeNo) { animeCaptionRepository.findAllWithAccountByAnimeNoOrderByUpdDtDesc(animeNo).map { AnimeCaptionDto(it) } }
            .also { animeRankService.hitAsync(animeNo, request.remoteAddr) }

}