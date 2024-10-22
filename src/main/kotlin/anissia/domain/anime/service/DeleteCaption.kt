package anissia.domain.anime.service

import anissia.domain.anime.model.DeleteCaptionCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface DeleteCaption {
    fun handle(cmd: DeleteCaptionCommand, session: Session): ResultWrapper<Unit>
}
