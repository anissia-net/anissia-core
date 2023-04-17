package anissia.domain.session.core.ports.inbound

import anissia.domain.session.core.model.DoLoginCommand
import anissia.domain.session.core.model.LoginInfoItem
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface DoLogin {
    fun handle(cmd: DoLoginCommand, session: Session): ResultWrapper<LoginInfoItem>
}
