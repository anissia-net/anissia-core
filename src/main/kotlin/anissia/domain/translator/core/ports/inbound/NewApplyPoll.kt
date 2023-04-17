package anissia.domain.translator.core.ports.inbound

import anissia.domain.session.core.model.Session
import anissia.domain.translator.core.model.NewApplyPollCommand
import anissia.shared.ResultWrapper

interface NewApplyPoll {
    fun handle(cmd: NewApplyPollCommand, session: Session): ResultWrapper<Unit>
}
