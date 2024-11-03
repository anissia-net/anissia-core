package anissia.domain.account.service

import anissia.domain.account.command.CompleteRegisterCommand
import anissia.domain.account.command.RequestRegisterCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface RegisterService {
    fun request(cmd: RequestRegisterCommand, session: Session): ResultWrapper<Unit>
    fun complete(cmd: CompleteRegisterCommand): ResultWrapper<Unit>
}
