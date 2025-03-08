package anissia.domain.session.service

import anissia.domain.session.command.GetJwtAuthInfoCommand
import anissia.domain.session.model.JwtAuthInfoItem
import anissia.domain.session.model.JwtKeyItem
import anissia.domain.session.model.SessionItem
import anissia.shared.ResultWrapper
import me.saro.jwt.JwtKey
import me.saro.jwt.impl.JwtEsAlgorithm

interface JwtService {
    fun getKey(kid: String): JwtKey?
    fun getKeyItem(): JwtKeyItem
    fun getAuthInfo(cmd: GetJwtAuthInfoCommand): ResultWrapper<JwtAuthInfoItem>
    fun renewKeyStore()
    fun updateAuthInfo(sessionItem: SessionItem): ResultWrapper<JwtAuthInfoItem>
    fun alg(): JwtEsAlgorithm
}
