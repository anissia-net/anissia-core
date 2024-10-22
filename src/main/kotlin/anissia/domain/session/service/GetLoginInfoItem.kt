package anissia.domain.session.service

import anissia.domain.session.model.GetLoginInfoItemCommand
import anissia.domain.session.model.LoginInfoItem
import anissia.shared.ResultWrapper

interface GetLoginInfoItem {
    fun handle(cmd: GetLoginInfoItemCommand): ResultWrapper<LoginInfoItem>
}
