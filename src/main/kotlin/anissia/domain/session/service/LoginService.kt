package anissia.domain.session.service

import anissia.domain.session.command.DoTokenLoginCommand
import anissia.domain.session.command.DoUserLoginCommand
import anissia.domain.session.model.JwtAuthInfoItem
import anissia.domain.session.model.SessionItem
import reactor.core.publisher.Mono

interface LoginService {
    fun doUserLogin(cmd: DoUserLoginCommand, sessionItem: SessionItem): Mono<JwtAuthInfoItem>
    fun doTokenLogin(cmd: DoTokenLoginCommand, sessionItem: SessionItem): Mono<JwtAuthInfoItem>
}
