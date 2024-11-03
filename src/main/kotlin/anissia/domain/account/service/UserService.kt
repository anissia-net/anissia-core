package anissia.domain.account.service

import anissia.domain.account.model.AccountUserItem
import anissia.domain.account.command.EditUserNameCommand
import anissia.domain.account.command.EditUserPasswordCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface UserService {
    fun get(session: Session): AccountUserItem
    fun editName(cmd: EditUserNameCommand, session: Session): ResultWrapper<Unit>
    fun editPassword(cmd: EditUserPasswordCommand, session: Session): ResultWrapper<Unit>
}
