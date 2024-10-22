package anissia.domain.translator.service

import anissia.domain.session.model.Session
import anissia.domain.translator.core.model.NewApplyPollCommand
import anissia.shared.ResultWrapper

interface NewApplyPoll {
    fun handle(cmd: NewApplyPollCommand, session: Session): ResultWrapper<Unit>
}
