package anissia.domain.account.service

import anissia.domain.account.model.EditUserNameCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface EditUserName {
    fun handle(cmd: EditUserNameCommand, session: Session): ResultWrapper<Unit>
}
