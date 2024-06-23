package anissia.domain.session.core.model

import anissia.domain.session.core.JwtKeyPair
import me.saro.jwt.alg.es.JwtEs256
import me.saro.jwt.core.JwtAlgorithm
import me.saro.jwt.core.JwtKey


class JwtKeyItem(
    val kid: String,
    val key: JwtKey,
) {
    companion object {
        fun parse(data: String, jwtEs256: JwtEs256): JwtKeyItem {
            val point = data.indexOf(' ')
            return JwtKeyItem(
                data.substring(0, point),
                jwtEs256.toJwtKeyByStringify(data.substring(point + 1))
            )
        }
        fun create(jwtKeyPair: JwtKeyPair, jwtAlgorithm: JwtAlgorithm): JwtKeyItem =
            JwtKeyItem(jwtKeyPair.kid.toString(), jwtAlgorithm.toJwtKeyByStringify(jwtKeyPair.data))
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
