package anissia.domain.session.service

import anissia.domain.session.model.LoginInfoItem
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface UpdateJwt {
    fun handle(session: Session): ResultWrapper<LoginInfoItem>
}
