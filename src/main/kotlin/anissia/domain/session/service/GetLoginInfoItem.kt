package anissia.domain.session.service

import anissia.domain.session.command.GetLoginInfoItemCommand
import anissia.domain.session.model.LoginInfoItem
import anissia.shared.ResultWrapper

interface GetLoginInfoItem {
    fun handle(cmd: GetLoginInfoItemCommand): ResultWrapper<LoginInfoItem>
}
