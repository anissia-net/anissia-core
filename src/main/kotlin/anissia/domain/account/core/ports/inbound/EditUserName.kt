package anissia.domain.account.core.ports.inbound

import anissia.domain.account.core.model.EditUserNameCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface EditUserName {
    fun handle(cmd: EditUserNameCommand, session: Session): ResultWrapper<Unit>
}
