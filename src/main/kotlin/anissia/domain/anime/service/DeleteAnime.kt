package anissia.domain.anime.service

import anissia.domain.anime.model.DeleteAnimeCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface DeleteAnime {
    fun handle(cmd: DeleteAnimeCommand, session: Session): ResultWrapper<Unit>
}
