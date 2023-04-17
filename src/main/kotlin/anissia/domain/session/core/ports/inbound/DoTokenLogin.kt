package anissia.domain.session.core.ports.inbound

import anissia.domain.session.core.model.DoTokenLoginCommand
import anissia.domain.session.core.model.LoginInfoItem
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface DoTokenLogin {
    fun handle(cmd: DoTokenLoginCommand, session: Session): ResultWrapper<LoginInfoItem>
}
