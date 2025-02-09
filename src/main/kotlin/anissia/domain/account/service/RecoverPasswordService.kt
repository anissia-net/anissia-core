package anissia.domain.account.service

import anissia.domain.account.command.CompleteRecoverPasswordCommand
import anissia.domain.account.command.RequestRecoverPasswordCommand
import anissia.domain.account.command.ValidateRecoverPasswordCommand
import anissia.domain.session.model.SessionItem
import anissia.shared.ResultWrapper

interface RecoverPasswordService {
    fun request(cmd: RequestRecoverPasswordCommand, sessionItem: SessionItem): Mono<String>
    fun validate(cmd: ValidateRecoverPasswordCommand): Mono<String>
    fun complete(cmd: CompleteRecoverPasswordCommand): Mono<String>
}
