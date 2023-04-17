package anissia.domain.translator.core.ports.inbound

import anissia.domain.session.core.model.Session
import anissia.domain.translator.core.model.NewApplyCommand
import anissia.shared.ResultWrapper

interface NewApply {
    fun handle(cmd: NewApplyCommand, session: Session): ResultWrapper<Long>
}
