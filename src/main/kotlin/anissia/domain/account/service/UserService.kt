package anissia.domain.account.service

import anissia.domain.account.command.EditUserNameCommand
import anissia.domain.account.command.EditUserPasswordCommand
import anissia.domain.account.model.AccountUserItem
import anissia.domain.session.model.SessionItem
import reactor.core.publisher.Mono

interface UserService {
    fun get(sessionItem: SessionItem): Mono<AccountUserItem>
    fun editName(cmd: EditUserNameCommand, sessionItem: SessionItem): Mono<String>
    fun editPassword(cmd: EditUserPasswordCommand, sessionItem: SessionItem): Mono<String>
}
