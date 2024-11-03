package anissia.domain.anime.service

import anissia.domain.anime.command.GetAnimeRankCommand
import anissia.domain.anime.command.HitAnimeCommand
import anissia.domain.session.model.SessionItem

interface AnimeRankService {
    fun get(cmd: GetAnimeRankCommand): List<Map<*,*>>
    fun hit(cmd: HitAnimeCommand, sessionItem: SessionItem)
    fun renew()
}
