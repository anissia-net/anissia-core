package anissia.domain.account.core.ports.inbound

import anissia.domain.account.core.model.RecoverCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface Recover {
    fun handle(cmd: RecoverCommand, session: Session): ResultWrapper<Unit>
}
