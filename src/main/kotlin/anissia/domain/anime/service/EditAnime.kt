package anissia.domain.anime.service

import anissia.domain.anime.model.EditAnimeCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface EditAnime {
    fun handle(cmd: EditAnimeCommand, session: Session): ResultWrapper<Long>
}
