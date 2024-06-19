package anissia.domain.session.infrastructure

import anissia.domain.session.core.model.JwtKeyItem
import anissia.domain.session.core.ports.outbound.JwtKeyPairRepository
import me.saro.jwt.alg.es.JwtEs256
import me.saro.jwt.core.JwtAlgorithm
import me.saro.jwt.core.JwtKey
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

@Service
class JwtService(
    val algorithm: JwtAlgorithm = JwtEs256(),
    private val jwtKeyPairRepository: JwtKeyPairRepository,
    private val keyStore: Deque<JwtKeyItem> = ConcurrentLinkedDeque(),
) {
    fun updateKeyStore() {
        val list = jwtKeyPairRepository.findAllByOrderByKidDesc()
            .reversed()
            .map { JwtKeyItem(it.kid.toString(), algorithm.toJwtKey(it.data)) }

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
