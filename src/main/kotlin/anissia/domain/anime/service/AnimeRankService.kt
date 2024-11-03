package anissia.domain.anime.service

import anissia.domain.anime.model.GetAnimeRankCommand
import anissia.domain.anime.model.HitAnimeCommand
import anissia.domain.session.model.Session

interface AnimeRankService {
    fun get(cmd: GetAnimeRankCommand): List<Map<*,*>>
    fun hit(cmd: HitAnimeCommand, session: Session)
    fun renew()
}
