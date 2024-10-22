package anissia.domain.anime.service

import anissia.domain.anime.model.NewCaptionCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface NewCaption {
    fun handle(cmd: NewCaptionCommand, session: Session): ResultWrapper<Unit>
}
