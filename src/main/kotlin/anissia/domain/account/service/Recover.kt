package anissia.domain.account.service

import anissia.domain.account.model.RecoverCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface Recover {
    fun handle(cmd: RecoverCommand, session: Session): ResultWrapper<Unit>
}
