package anissia.domain.anime.service

import anissia.domain.anime.model.AnimeItem
import anissia.domain.anime.model.GetAnimeListCommand
import org.springframework.data.domain.Page

interface GetAnimeList {
    fun handle(cmd: GetAnimeListCommand): Page<AnimeItem>
}
