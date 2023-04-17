package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.GetAnimeAutocorrectCommand

interface GetAnimeAutocorrect {
    fun handle(cmd: GetAnimeAutocorrectCommand): List<String>
}
