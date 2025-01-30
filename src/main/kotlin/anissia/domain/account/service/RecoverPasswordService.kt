package anissia.domain.account.service

import anissia.domain.account.command.CompleteRecoverPasswordCommand
import anissia.domain.account.command.RequestRecoverPasswordCommand
import anissia.domain.account.command.ValidateRecoverPasswordCommand
import anissia.domain.session.model.SessionItem
import reactor.core.publisher.Mono

interface RecoverPasswordService {
    fun request(cmd: RequestRecoverPasswordCommand, sessionItem: SessionItem): Mono<Void>
    fun validate(cmd: ValidateRecoverPasswordCommand): Mono<Void>
    fun complete(cmd: CompleteRecoverPasswordCommand): Mono<Void>
}
