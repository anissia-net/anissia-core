package anissia.domain.account.service

import anissia.domain.account.model.EditUserNameCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface EditUserName {
    fun handle(cmd: EditUserNameCommand, session: Session): ResultWrapper<Unit>
}
