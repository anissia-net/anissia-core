package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.GetAnimeRankCommand

interface GetAnimeRank {
    fun handle(cmd: GetAnimeRankCommand): List<Map<*,*>>

    fun clearCache()
}
