package anissia.domain.anime.service

import anissia.domain.anime.model.AnimeItem
import anissia.domain.anime.model.GetAnimeCommand
import anissia.domain.anime.model.HitAnimeCommand
import anissia.domain.anime.repository.AnimeRepository
import anissia.domain.session.model.Session
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
