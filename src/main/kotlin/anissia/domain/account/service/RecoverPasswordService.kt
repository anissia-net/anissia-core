package anissia.domain.account.service

import anissia.domain.account.command.CompleteRecoverPasswordCommand
import anissia.domain.account.command.RequestRecoverPasswordCommand
import anissia.domain.account.command.ValidateRecoverPasswordCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface RecoverPasswordService {
    fun request(cmd: RequestRecoverPasswordCommand, session: Session): ResultWrapper<Unit>
    fun validate(cmd: ValidateRecoverPasswordCommand): ResultWrapper<Unit>
    fun complete(cmd: CompleteRecoverPasswordCommand): ResultWrapper<Unit>
}
