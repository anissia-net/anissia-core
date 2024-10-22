package anissia.domain.account.core.service

import anissia.domain.account.core.model.EditUserPasswordCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface EditUserPassword {
    fun handle(cmd: EditUserPasswordCommand, session: Session): ResultWrapper<Unit>
}
