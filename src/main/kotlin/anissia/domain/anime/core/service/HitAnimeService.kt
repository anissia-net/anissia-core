package anissia.domain.anime.core.service

import anissia.domain.anime.core.AnimeHit
import anissia.domain.anime.core.model.HitAnimeCommand
import anissia.domain.anime.core.ports.inbound.HitAnime
import anissia.domain.anime.core.ports.outbound.AnimeHitRepository
import anissia.domain.session.core.model.Session
import anissia.infrastructure.common.As
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class HitAnimeService(
    private val animeHitRepository: AnimeHitRepository,
): HitAnime {
    @Async
    override fun handle(cmd: HitAnimeCommand, session: Session) {
        animeHitRepository.save(AnimeHit(animeNo = cmd.animeNo, ip = session.ip, hour = OffsetDateTime.now().format(As.DTF_RANK_HOUR).toLong()))
    }
}
