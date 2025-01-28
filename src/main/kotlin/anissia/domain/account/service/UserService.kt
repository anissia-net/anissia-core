package anissia.domain.account.service

import anissia.domain.account.command.EditUserNameCommand
import anissia.domain.account.command.EditUserPasswordCommand
import anissia.domain.account.model.AccountUserItem
import anissia.domain.session.model.SessionItem
import anissia.shared.ApiResponse

interface UserService {
    fun get(sessionItem: SessionItem): AccountUserItem
    fun editName(cmd: EditUserNameCommand, sessionItem: SessionItem): ApiResponse<Unit>
    fun editPassword(cmd: EditUserPasswordCommand, sessionItem: SessionItem): ApiResponse<Unit>
}
