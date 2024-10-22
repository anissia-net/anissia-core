package anissia.domain.session.infrastructure

import anissia.domain.session.model.JwtKeyItem
import anissia.domain.session.repository.JwtKeyPairRepository
import me.saro.jwt.alg.es.JwtEs256
import me.saro.jwt.core.Jwt
import me.saro.jwt.core.JwtKey
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

@Service
class JwtService(
    val es256: JwtEs256 = Jwt.es256(),
    private val jwtKeyPairRepository: JwtKeyPairRepository,
    private val keyStore: Deque<JwtKeyItem> = ConcurrentLinkedDeque(),
) {
    fun updateKeyStore() {
        val list = jwtKeyPairRepository.findAllByOrderByKidDesc()
            .reversed()
            .map { JwtKeyItem(it.kid.toString(), es256.toJwtKeyByStringify(it.data)) }

        list.forEach {
            if (!keyStore.contains(it)) {
                keyStore.addFirst(it)
            }
        }
        keyStore.removeIf { !list.contains(it) }
    }

    fun getKeyItem(): JwtKeyItem =
        keyStore.elementAt(1)

    fun findKey(kid: String): JwtKey? =
        keyStore.find { it.kid == kid }?.key
}
