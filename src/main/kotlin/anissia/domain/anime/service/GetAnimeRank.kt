package anissia.domain.anime.service

import anissia.domain.anime.model.GetAnimeRankCommand

interface GetAnimeRank {
    fun handle(cmd: GetAnimeRankCommand): List<Map<*,*>>

    fun clearCache()
}
