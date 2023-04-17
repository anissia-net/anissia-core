package anissia.domain.anime.core.service

import anissia.domain.anime.core.model.AnimeItem
import anissia.domain.anime.core.model.GetAnimeCommand
import anissia.domain.anime.core.model.HitAnimeCommand
import anissia.domain.anime.core.ports.inbound.GetAnime
import anissia.domain.anime.core.ports.inbound.HitAnime
import anissia.domain.anime.core.ports.outbound.AnimeRepository
import anissia.domain.session.core.model.Session
import org.springframework.stereotype.Service

@Service
class GetAnimeService(
    private val animeRepository: AnimeRepository,
    private val hitAnime: HitAnime,
): GetAnime {
    override fun handle(cmd: GetAnimeCommand, session: Session): AnimeItem =
        animeRepository.findWithCaptionsByAnimeNo(cmd.animeNo)
            ?.let { AnimeItem(it, true) }
            ?.also { hitAnime.handle(HitAnimeCommand(cmd.animeNo), session) }
            ?: AnimeItem()
}
