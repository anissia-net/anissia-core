package anissia.domain.anime.service

import anissia.domain.anime.command.GetAnimeRankCommand
import anissia.domain.anime.command.HitAnimeCommand
import anissia.domain.session.model.Session

interface AnimeRankService {
    fun get(cmd: GetAnimeRankCommand): List<Map<*,*>>
    fun hit(cmd: HitAnimeCommand, session: Session)
    fun renew()
}
