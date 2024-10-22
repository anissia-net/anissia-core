package anissia.domain.account.service

import anissia.domain.account.model.RegisterCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface Register {
    fun handle(cmd: RegisterCommand, session: Session): ResultWrapper<Unit>
}
