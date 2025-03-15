package anissia.domain.session.service

import anissia.domain.session.model.SessionItem
import me.saro.jwt.JwtNode

interface JwtService {
    fun issue()
    fun createJwt(sessionItem: SessionItem): String
    fun parseJwt(jwt: String): JwtNode
}
