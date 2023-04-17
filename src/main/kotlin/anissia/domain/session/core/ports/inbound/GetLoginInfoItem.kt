package anissia.domain.session.core.ports.inbound

import anissia.domain.session.core.model.GetLoginInfoItemCommand
import anissia.domain.session.core.model.LoginInfoItem
import anissia.shared.ResultWrapper

interface GetLoginInfoItem {
    fun handle(cmd: GetLoginInfoItemCommand): ResultWrapper<LoginInfoItem>
}
