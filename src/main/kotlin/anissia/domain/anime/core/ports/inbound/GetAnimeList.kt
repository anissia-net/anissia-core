package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.AnimeItem
import anissia.domain.anime.core.model.GetAnimeListCommand
import org.springframework.data.domain.Page

interface GetAnimeList {
    fun handle(cmd: GetAnimeListCommand): Page<AnimeItem>
}
