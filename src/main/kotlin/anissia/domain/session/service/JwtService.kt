package anissia.domain.session.service

import anissia.domain.session.command.GetJwtAuthInfoCommand
import anissia.domain.session.model.JwtAuthInfoItem
import anissia.domain.session.model.JwtKeyItem
import anissia.domain.session.model.SessionItem
import anissia.shared.ApiResponse
import me.saro.jwt.alg.es.JwtEs256
import me.saro.jwt.core.JwtKey

interface JwtService {
    fun getKey(kid: String): JwtKey?
    fun getKeyItem(): JwtKeyItem
    fun getAuthInfo(cmd: GetJwtAuthInfoCommand): ApiResponse<JwtAuthInfoItem>
    fun renewKeyStore()
    fun updateAuthInfo(sessionItem: SessionItem): ApiResponse<JwtAuthInfoItem>
    fun alg(): JwtEs256
}
