package anissia.domain.anime.service

import anissia.domain.anime.model.GetAnimeAutocorrectCommand

interface GetAnimeAutocorrect {
    fun handle(cmd: GetAnimeAutocorrectCommand): List<String>
}
