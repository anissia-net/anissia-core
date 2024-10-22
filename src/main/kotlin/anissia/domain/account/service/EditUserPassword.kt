package anissia.domain.account.service

import anissia.domain.account.model.EditUserPasswordCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface EditUserPassword {
    fun handle(cmd: EditUserPasswordCommand, session: Session): ResultWrapper<Unit>
}
