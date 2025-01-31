package anissia.domain.account.service

import anissia.domain.account.command.CompleteRegisterCommand
import anissia.domain.account.command.RequestRegisterCommand
import anissia.domain.session.model.SessionItem
import reactor.core.publisher.Mono

interface RegisterService {
    fun request(cmd: RequestRegisterCommand, sessionItem: SessionItem): Mono<String>
    fun complete(cmd: CompleteRegisterCommand): Mono<String>
}
