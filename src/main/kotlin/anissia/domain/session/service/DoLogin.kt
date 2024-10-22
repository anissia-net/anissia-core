package anissia.domain.session.service

import anissia.domain.session.model.DoLoginCommand
import anissia.domain.session.model.LoginInfoItem
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface DoLogin {
    fun handle(cmd: DoLoginCommand, session: Session): ResultWrapper<LoginInfoItem>
}
