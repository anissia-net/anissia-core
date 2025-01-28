package anissia.domain.account.service

import anissia.domain.account.command.CompleteRecoverPasswordCommand
import anissia.domain.account.command.RequestRecoverPasswordCommand
import anissia.domain.account.command.ValidateRecoverPasswordCommand
import anissia.domain.session.model.SessionItem
import anissia.shared.ApiResponse

interface RecoverPasswordService {
    fun request(cmd: RequestRecoverPasswordCommand, sessionItem: SessionItem): ApiResponse<Void>
    fun validate(cmd: ValidateRecoverPasswordCommand): ApiResponse<Void>
    fun complete(cmd: CompleteRecoverPasswordCommand): ApiResponse<Void>
}
