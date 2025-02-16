package anissia.domain.session.service

import anissia.domain.session.command.GetJwtAuthInfoCommand
import anissia.domain.session.model.JwtAuthInfoItem
import anissia.domain.session.model.SessionItem
import reactor.core.publisher.Mono

interface JwtService {
    fun registerNewJwtKey(): Mono<String>
    fun renewKeyStore(): Mono<String>
    fun toSessionItem(jwt: String, ip: String): Mono<SessionItem>
    fun updateAuthInfo(sessionItem: SessionItem): Mono<JwtAuthInfoItem>
    fun getAuthInfo(cmd: GetJwtAuthInfoCommand): Mono<JwtAuthInfoItem>
}
