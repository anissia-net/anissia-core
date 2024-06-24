package anissia.domain.anime.core.service

import anissia.domain.anime.core.AnimeStore
import anissia.domain.anime.core.model.AnimeRankItem
import anissia.domain.anime.core.ports.inbound.UpdateAnimeRank
import anissia.domain.anime.core.ports.outbound.AnimeHitHourRepository
import anissia.domain.anime.core.ports.outbound.AnimeHitRepository
import anissia.domain.anime.core.ports.outbound.AnimeStoreRepository
import anissia.infrastructure.common.As
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UpdateAnimeRankService(
    private val animeHitRepository: AnimeHitRepository,
    private val animeHitHourRepository: AnimeHitHourRepository,
    private val animeStoreRepository: AnimeStoreRepository,
    private val animeRankService: GetAnimeRankService
): UpdateAnimeRank {

    @Transactional
    override fun handle() {
        // step 1. remove hit history older then 1000 days
        animeHitHourRepository.deleteByHourLessThan(LocalDateTime.now().minusDays(1000).format(As.DTF_RANK_HOUR).toLong())
        // step 2. merge anime hits
        mergeAnimeHit()
        // step 3. extract and bind rank
        extractAllRank()
    }

    private fun mergeAnimeHit() {
        val hour = LocalDateTime.now().format(As.DTF_RANK_HOUR)
        animeHitRepository.extractAllAnimeHitHour(hour.toLong())
            .map { r -> animeHitHourRepository.findById(r.key).map { it.hit += r.hit; it }.orElse(r) }
            .also { animeHitHourRepository.saveAll(it) }
            .also { animeHitRepository.deleteByHourLessThan(hour.toLong()) }
    }

    private fun extractAllRank() {
        val dt = LocalDateTime.now()

        // year rank (364 days, diff 392 days)
        val day392List = extractRank(dt.minusDays(392).format(As.DTF_RANK_HOUR))
        val day364List = extractRank(dt.minusDays(364).format(As.DTF_RANK_HOUR))
            .apply { calculateRankDiff(this, day392List) }

        // quarter rank (84 days, diff 112 days)
        val day112List = extractRank(dt.minusDays(112).format(As.DTF_RANK_HOUR))
        val day84List = extractRank(dt.minusDays(84).format(As.DTF_RANK_HOUR))
            .apply { calculateRankDiff(this, day112List) }

        // week rank (week 7 days, diff 14 days)
        val day14List = extractRank(dt.minusDays(14).format(As.DTF_RANK_HOUR))
        val day7List = extractRank(dt.minusDays(7).format(As.DTF_RANK_HOUR))
            .apply { calculateRankDiff(this, day14List) }

        animeStoreRepository.save(AnimeStore("rank.week", "", toString(day7List)))
        animeStoreRepository.save(AnimeStore("rank.quarter", "", toString(day84List)))
        animeStoreRepository.save(AnimeStore("rank.year", "", toString(day364List)))
        animeRankService.clearCache()
    }

    private fun toString(list: List<AnimeRankItem>): String = list.run { As.toJsonString(if (size > 30) list.subList(0, 30) else list) }

    private fun extractRank(startHour: String): List<AnimeRankItem> =
        animeHitHourRepository
                .extractAllAnimeRank(startHour.toLong())
                .filter { it.exist }
                .apply { calculateRank(this) }

    private fun calculateRank(animeRank: List<AnimeRankItem>): List<AnimeRankItem> =
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

    private fun calculateRankDiff(rankList: List<AnimeRankItem>, prevRankList: List<AnimeRankItem>) =
        rankList.forEach { now ->
            prevRankList
                    .find { prev -> prev.animeNo == now.animeNo }
                    ?.also { prev -> now.diff = -(now.rank - prev.rank) }
        }


}
