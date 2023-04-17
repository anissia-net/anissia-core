package anissia.domain.account.core.ports.inbound

import anissia.domain.account.core.model.RegisterCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface Register {
    fun handle(cmd: RegisterCommand, session: Session): ResultWrapper<Unit>
}
