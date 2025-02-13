package anissia.domain.anime.service

import anissia.domain.anime.AnimeHit
import anissia.domain.anime.AnimeStore
import anissia.domain.anime.command.GetAnimeRankCommand
import anissia.domain.anime.command.HitAnimeCommand
import anissia.domain.anime.model.AnimeRankItem
import anissia.domain.anime.repository.AnimeHitHourRepository
import anissia.domain.anime.repository.AnimeHitRepository
import anissia.domain.anime.repository.AnimeStoreRepository
import anissia.domain.session.model.SessionItem
import anissia.infrastructure.common.DTF_RANK_HOUR
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import me.saro.kit.service.CacheStore
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.OffsetDateTime

@Service
class AnimeRankServiceImpl(
    private val animeStoreRepository: AnimeStoreRepository,
    private val animeHitRepository: AnimeHitRepository,
    private val animeHitHourRepository: AnimeHitHourRepository,
): AnimeRankService {

    private val rankCacheStore = CacheStore<String, List<Map<*,*>>>((5 * 60000).toLong())
    private val objectMapper = ObjectMapper()
    private val tr = object: TypeReference<List<Map<*, *>>>() {}

    override fun get(cmd: GetAnimeRankCommand): Mono<List<Map<*, *>>> = rankCacheStore.find(cmd.type) { type ->
        when (type) {
            "week", "quarter", "year" ->
                objectMapper.readValue(animeStoreRepository.findByIdOrNull("rank.$type")?.data ?: "[]", tr)
            else -> listOf()
        }
    }

    @Async
    override fun hit(cmd: HitAnimeCommand, sessionItem: SessionItem) {
        animeHitRepository.save(AnimeHit(animeNo = cmd.animeNo, ip = sessionItem.ip, hour = OffsetDateTime.now().format(DTF_RANK_HOUR).toLong()))
    }

    private fun clearCache() {
        rankCacheStore.clear()
    }

    @Transactional
    override fun renew() {
        // step 1. remove hit history older then 1000 days
        animeHitHourRepository.deleteByHourLessThan(LocalDateTime.now().minusDays(1000).format(DTF_RANK_HOUR).toLong())
        // step 2. merge anime hits
        mergeAnimeHit()
        // step 3. extract and bind rank
        extractAllRank()
    }

    private fun mergeAnimeHit() {
        // merge by hour
        val hour = LocalDateTime.now().format(DTF_RANK_HOUR)
        animeHitRepository.extractAllAnimeHitHour(hour.toLong())
            .map { r -> animeHitHourRepository.findById(r.key).map { it.hit += r.hit; it }.orElse(r) }
            .also { animeHitHourRepository.saveAll(it) }
            .also { animeHitRepository.deleteByHourLessThan(hour.toLong()) }

        // merge by day
        animeHitHourRepository.mergeByDay(60)
        animeHitHourRepository.deleteMergedByDay(60)

        // merge by month
        animeHitHourRepository.mergeByMonth(400)
        animeHitHourRepository.deleteMergedByMonth(400)
    }

    private fun extractAllRank() {
        val dt = LocalDateTime.now()

        // year rank (364 days, diff 392 days)
        val day392List = extractRank(dt.minusDays(392).format(DTF_RANK_HOUR))
        val day364List = extractRank(dt.minusDays(364).format(DTF_RANK_HOUR))
            .apply { calculateRankDiff(this, day392List) }

        // quarter rank (84 days, diff 112 days)
        val day112List = extractRank(dt.minusDays(112).format(DTF_RANK_HOUR))
        val day84List = extractRank(dt.minusDays(84).format(DTF_RANK_HOUR))
            .apply { calculateRankDiff(this, day112List) }

        // week rank (week 7 days, diff 14 days)
        val day14List = extractRank(dt.minusDays(14).format(DTF_RANK_HOUR))
        val day7List = extractRank(dt.minusDays(7).format(DTF_RANK_HOUR))
            .apply { calculateRankDiff(this, day14List) }

        animeStoreRepository.save(AnimeStore("rank.week", "", toString(day7List)))
        animeStoreRepository.save(AnimeStore("rank.quarter", "", toString(day84List)))
        animeStoreRepository.save(AnimeStore("rank.year", "", toString(day364List)))
        clearCache()
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
