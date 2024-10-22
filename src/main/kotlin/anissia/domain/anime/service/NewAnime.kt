package anissia.domain.anime.service

import anissia.domain.anime.model.NewAnimeCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface NewAnime {
    fun handle(cmd: NewAnimeCommand, session: Session): ResultWrapper<Long>
}
