package anissia.services

import anissia.dto.AnimeRankDto
import anissia.misc.As
import anissia.rdb.entity.AnimeHit
import anissia.rdb.entity.AnimeStore
import anissia.rdb.repository.AnimeHitHourRepository
import anissia.rdb.repository.AnimeHitRepository
import anissia.rdb.repository.AnimeStoreRepository
import me.saro.kit.CacheStore
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AnimeRankService(
    private val animeHitRepository: AnimeHitRepository,
    private val animeHitHourRepository: AnimeHitHourRepository,
    private val animeStoreRepository: AnimeStoreRepository
) {
    private val rankCacheStore = CacheStore<String, String>((5 * 60000).toLong())

    fun getRank(type: String): String =
        when (type) {
            "week", "month", "quarter" ->
                rankCacheStore.find(type) { animeStoreRepository.findById("rank.$type").map { it.data }.orElse("[]") }
            else ->
                "[]"
        }

    /**
     * anime rank batch
     */
    @Transactional
    fun animeRankBatch() {
        // step 1. remove hit history older then 365 days
        animeHitHourRepository.deleteByHourLessThan(LocalDateTime.now().minusDays(365).format(As.DTF_RANK_HOUR).toLong())
        // step 2. merge anime hits
        mergeAnimeHit()
        // step 3. extract and bind rank
        extractAllRank()
    }

    @Async
    fun hitAsync(animeNo: Long, ip: String, hour: Long = LocalDateTime.now().format(As.DTF_RANK_HOUR).toLong()) =
        animeHitRepository.save(AnimeHit(animeNo = animeNo, ip = ip, hour = hour))

    private fun mergeAnimeHit() {
        val hour = LocalDateTime.now().format(As.DTF_RANK_HOUR)
        animeHitRepository.extractAllAnimeHitHour(hour.toLong())
            .map { r -> animeHitHourRepository.findById(r.key).map { it.hit += r.hit; it }.orElse(r) }
            .also { animeHitHourRepository.saveAll(it) }
            .also { animeHitRepository.deleteByHourLessThan(hour.toLong()) }
    }

    private fun extractAllRank() {
        val dt = LocalDateTime.now()
        val day112List = extractRank(dt.minusDays(112).format(As.DTF_RANK_HOUR))
        val day84List = extractRank(dt.minusDays(84).format(As.DTF_RANK_HOUR)).apply { calculateRankDiff(this, day112List) }
        val day35List = extractRank(dt.minusDays(35).format(As.DTF_RANK_HOUR))
        val day28List = extractRank(dt.minusDays(28).format(As.DTF_RANK_HOUR)).apply { calculateRankDiff(this, day35List) }
        val day14List = extractRank(dt.minusDays(14).format(As.DTF_RANK_HOUR))
        val day7List = extractRank(dt.minusDays(7).format(As.DTF_RANK_HOUR)).apply { calculateRankDiff(this, day14List) }

        animeStoreRepository.save(AnimeStore("rank.week", "", toString(day7List)))
        animeStoreRepository.save(AnimeStore("rank.month", "", toString(day28List)))
        animeStoreRepository.save(AnimeStore("rank.quarter", "", toString(day84List)))
        rankCacheStore.clear()
    }

    private fun toString(list: List<AnimeRankDto>): String = list.run { As.toJsonString(if (size > 30) list.subList(0, 30) else list) }

    private fun extractRank(startHour: String): List<AnimeRankDto> =
        animeHitHourRepository
                .extractAllAnimeRank(startHour.toLong())
                .filter { it.exist }
                .apply { calculateRank(this) }

    private fun calculateRank(animeRank: List<AnimeRankDto>): List<AnimeRankDto> =
            animeRank.apply {
                var rank = 0
                var hit = -1L
                forEachIndexed { index, node ->
                    if (node.hit != hit) {
                        hit = node.hit
                        rank = index + 1
                    }
                    node.rank = rank
                }
            }

    private fun calculateRankDiff(rankList: List<AnimeRankDto>, prevRankList: List<AnimeRankDto>) =
        rankList.forEach { now ->
            prevRankList
                    .find { prev -> prev.animeNo == now.animeNo }
                    ?.also { prev -> now.diff = -(now.rank - prev.rank) }
        }
}
