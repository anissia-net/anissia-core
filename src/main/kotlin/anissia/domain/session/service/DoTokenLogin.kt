package anissia.domain.session.service

import anissia.domain.session.model.DoTokenLoginCommand
import anissia.domain.session.model.LoginInfoItem
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface DoTokenLogin {
    fun handle(cmd: DoTokenLoginCommand, session: Session): ResultWrapper<LoginInfoItem>
}
