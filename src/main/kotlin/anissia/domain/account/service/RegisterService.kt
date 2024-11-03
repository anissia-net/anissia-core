package anissia.domain.account.service

import anissia.domain.account.command.CompleteRegisterCommand
import anissia.domain.account.command.RequestRegisterCommand
import anissia.domain.session.model.SessionItem
import anissia.shared.ResultWrapper

interface RegisterService {
    fun request(cmd: RequestRegisterCommand, sessionItem: SessionItem): ResultWrapper<Unit>
    fun complete(cmd: CompleteRegisterCommand): ResultWrapper<Unit>
}
