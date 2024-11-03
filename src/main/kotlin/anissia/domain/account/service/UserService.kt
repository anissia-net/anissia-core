package anissia.domain.account.service

import anissia.domain.account.model.AccountUserItem
import anissia.domain.account.command.EditUserNameCommand
import anissia.domain.account.command.EditUserPasswordCommand
import anissia.domain.session.model.SessionItem
import anissia.shared.ResultWrapper

interface UserService {
    fun get(sessionItem: SessionItem): AccountUserItem
    fun editName(cmd: EditUserNameCommand, sessionItem: SessionItem): ResultWrapper<Unit>
    fun editPassword(cmd: EditUserPasswordCommand, sessionItem: SessionItem): ResultWrapper<Unit>
}
