package anissia.domain.anime.core.service

import anissia.domain.anime.core.model.GetAnimeRankCommand
import anissia.domain.anime.core.ports.inbound.GetAnimeRank
import anissia.domain.anime.core.ports.outbound.AnimeStoreRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.saro.kit.CacheStore
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class GetAnimeRankService(
    private val animeStoreRepository: AnimeStoreRepository,
): GetAnimeRank {

    private val rankCacheStore = CacheStore<String, List<Map<*,*>>>((5 * 60000).toLong())
    private val objectMapper = jacksonObjectMapper()
    private val tr = object: TypeReference<List<Map<*, *>>>() {}

    override fun handle(cmd: GetAnimeRankCommand): List<Map<*,*>> = rankCacheStore.find(cmd.type) { type ->
        when (type) {
            "week", "quarter", "year" ->
                objectMapper.readValue(animeStoreRepository.findByIdOrNull("rank.$type")?.data ?: "[]", tr)
            else -> listOf()
        }
    }

    override fun clearCache() {
        rankCacheStore.clear()
    }
}
