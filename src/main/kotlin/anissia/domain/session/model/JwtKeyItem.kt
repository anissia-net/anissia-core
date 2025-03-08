package anissia.domain.session.model

import anissia.domain.session.JwtKeyPair
import me.saro.jwt.Jwt
import me.saro.jwt.JwtAlgorithm
import me.saro.jwt.JwtKey
import me.saro.jwt.impl.JwtEsAlgorithm


class JwtKeyItem(
    val kid: String,
    val key: JwtKey,
) {
    companion object {
        fun parse(data: String, jwtEs256: JwtEsAlgorithm): JwtKeyItem {
            val point = data.indexOf(' ')
            return JwtKeyItem(
                data.substring(0, point),
                Jwt.parseKey(data.substring(point + 1))
            )
        }
        fun create(jwtKeyPair: JwtKeyPair, jwtAlgorithm: JwtAlgorithm): JwtKeyItem =
            JwtKeyItem(jwtKeyPair.kid.toString(), Jwt.parseKey(jwtKeyPair.data))
    }

    override fun toString(): String =
        "$kid ${key.stringify}"

    override fun equals(other: Any?): Boolean {
        return (other is JwtKeyItem) && (other.kid == this.kid)
    }

    override fun hashCode(): Int {
        return kid.hashCode()
    }
}
