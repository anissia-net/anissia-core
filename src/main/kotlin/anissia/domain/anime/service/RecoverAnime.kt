package anissia.domain.anime.service

import anissia.domain.anime.model.RecoverAnimeCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface RecoverAnime {
    fun handle(cmd: RecoverAnimeCommand, session: Session): ResultWrapper<Long>
}
