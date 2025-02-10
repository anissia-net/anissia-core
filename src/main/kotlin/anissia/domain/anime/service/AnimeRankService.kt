package anissia.domain.anime.service

import anissia.domain.anime.command.GetAnimeRankCommand
import anissia.domain.anime.command.HitAnimeCommand
import anissia.domain.session.model.SessionItem
import reactor.core.Disposable
import reactor.core.publisher.Mono

interface AnimeRankService {
    fun get(cmd: GetAnimeRankCommand): Mono<List<Map<*, *>>>
    fun hit(cmd: HitAnimeCommand, sessionItem: SessionItem): Mono<String>
    fun renew(): Mono<String>
}
