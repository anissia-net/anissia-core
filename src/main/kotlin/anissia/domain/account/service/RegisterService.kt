package anissia.domain.account.service

import anissia.domain.account.command.CompleteRegisterCommand
import anissia.domain.account.command.RequestRegisterCommand
import anissia.domain.session.model.SessionItem
import anissia.shared.ApiResponse

interface RegisterService {
    fun request(cmd: RequestRegisterCommand, sessionItem: SessionItem): ApiResponse<Void>
    fun complete(cmd: CompleteRegisterCommand): ApiResponse<Void>
}
