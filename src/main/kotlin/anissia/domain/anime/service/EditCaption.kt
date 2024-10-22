package anissia.domain.anime.service

import anissia.domain.anime.model.EditCaptionCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface EditCaption {
    fun handle(cmd: EditCaptionCommand, session: Session): ResultWrapper<Unit>
}
